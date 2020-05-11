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

                    /*
                    error1 = name already in use
                    error2 = invalid Password
                    error3 = invalid Username
                    error4 = invalid command
                    error5 = user is already connected
                    connected = Registered/Logged in without errors
                     */

                    if (awnser.equals("RegU")) {
                        System.out.println("Register");
                        if (!server.getUsers().containsKey(nickname)) {
                            user = new User(nickname, password, server);
                            user.setSocket(socket);
                            this.server.getUsers().put(user.getName(), user);
                            loggedIn = true;
                        } else {
                            server.writeStringToSocket(socket, "error1");
                            continue;
                        }
                    } else {
                        System.out.println("Login");
                        if (server.getUsers().containsKey(nickname)) {
                            if (server.getUsers().get(nickname).checkPassword(password)) {
                                user = server.getUsers().get(nickname);
                                if (!user.isConnected()) {
                                    user.setSocket(socket);
                                    loggedIn = true;
                                }else {
                                    server.writeStringToSocket(socket, "error5");
                                    continue;
                                }
                            } else {
                                server.writeStringToSocket(socket, "error2");
                                continue;
                            }
                        } else {
                            server.writeStringToSocket(socket, "error3");
                            continue;
                        }
                    }
                    server.connectUser(user);
                    Thread t = new Thread(user);
                    t.start();
                    server.writeStringToSocket(socket,"connected");
                    System.out.println(nickname + " connected");
                    server.addClientThread(nickname, t);
                } else {
                    server.writeStringToSocket(socket, "error4");
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
