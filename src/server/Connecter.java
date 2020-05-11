package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Connecter implements Runnable{

    private Socket socket;
    private Server server;
    private Thread thread;

    public Connecter(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        System.out.println("Client connected via address: " + socket.getInetAddress().getHostAddress());
        System.out.println("Connected clients: " + this.server.getUsers().size());
        DataInputStream in = null;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean loggedIn = false;
        while(!loggedIn) {
            try {
                String input = in.readUTF();
                String awnser = input.substring(0, 4);
                if ((awnser.equals("RegU") || awnser.equals("LogU")) && input.length() >= 7) {
                    String nickname = input.substring(4, input.indexOf('|'));
                    String password = input.substring(input.indexOf('|'));
                    User user;

                    if (awnser.equals("RegU")) {
                        System.out.println("Register");
                        if (!server.getUsers().containsKey(nickname)) {
                            user = new User(nickname, password, server);
                            user.setSocket(socket);
                            this.server.getUsers().put(user.getName(), user);

                            loggedIn = true;
                        } else {
                            server.writeStringToSocket(socket, "Name already in use!");
                            continue;
                        }
                    } else {
                        System.out.println("Login");
                        if (server.getUsers().containsKey(nickname)) {
                            if (server.getUsers().get(nickname).checkPassword(password)) {
                                server.getUsers().get(nickname).setSocket(socket);
                                loggedIn = true;
                            } else {
                                server.writeStringToSocket(socket, "invalid Password!");
                                continue;
                            }
                        } else {
                            server.writeStringToSocket(socket, "invalid Username!");
                            continue;
                        }
                    }
                    Thread t = new Thread(server.getUsers().get(nickname));
                    t.start();
                    System.out.println(nickname + " connected");
                    server.addClientThread(nickname, t);
                } else {
                    server.writeStringToSocket(socket, "Invalid command");
                }
            }catch (IOException e){
                System.out.println("Unexpected connection loss");
                break;
            }
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
