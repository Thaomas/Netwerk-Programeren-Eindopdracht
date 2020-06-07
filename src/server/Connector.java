package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connector implements Runnable {

    private final Socket socket;
    private final Server server;

    public Connector(Socket socket, Server server) {
        this.socket = socket;
        System.out.println(socket.getPort());
        this.server = server;
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
        while (!loggedIn) {
            try {
                String input = in.readUTF();
                String answer = input.substring(0, 4);
                if ((answer.equals("RegU") || answer.equals("LogU") || answer.equals("ReDs")) && input.length() >= 7) {
                    String nickname = input.substring(4, input.indexOf('|'));
                    String password = input.substring(input.indexOf('|') + 1);
                    User user;
                    if (answer.equals("RegU")) {
                        System.out.println("Register");
                        if (!server.getUsers().containsKey(nickname)) {
                            user = new User(nickname, password, server);
                            server.addUser(user);
                            loggedIn = true;
                        } else {
                            respond("error1");
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
                                } else {
                                    respond("error5");
                                    continue;
                                }
                            } else {
                                respond("error2");
                                continue;
                            }
                        } else {
                            respond("error3");
                            continue;
                        }
                    }
                    server.save();

                    user.setSocket(socket);
                    server.connectUser(user);
                    respond("connected");
                    Thread t = new Thread(user);
                    t.start();
                    System.out.println(nickname + " connected");
                    server.addClientThread(nickname, t);
                } else {
                    //Invalid command
                    respond("error4");
                }
            } catch (IOException e) {
                System.out.println("Unexpected connection loss " + socket.getRemoteSocketAddress().toString());
                return;
            }
        }
    }

    public void respond(String response) {
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF(response);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
