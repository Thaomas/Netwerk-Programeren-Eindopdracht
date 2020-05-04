package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class Server {


    private final int port = 10000;
    private ServerSocket serverSocket;

    private ArrayList<ServerClient> clients = new ArrayList<>();
    //private ArrayList<Thread> clientThreads = new ArrayList<>();
    private HashMap<String, Thread> clientThreads = new HashMap<>();


    public static void main(String[] args) {

        System.out.println("Loading server");
        Server server = new Server();
        server.connect();
    }


    public void connect() {

        try {
            this.serverSocket = new ServerSocket(port);

            boolean isRunning = true;
            while (isRunning) {

                System.out.println("Waiting for clients...");
                Socket socket = this.serverSocket.accept();

                System.out.println("Client connected via address: " + socket.getInetAddress().getHostAddress());
                System.out.println("Connected clients: " + this.clients.size());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String nickname = in.readUTF();

                ServerClient serverClient = new ServerClient(socket, nickname, this);
                Thread t = new Thread(serverClient);
                t.start();
                this.clientThreads.put(nickname, t);
                this.clients.add(serverClient);

                for (ServerClient c : clients) {
                    c.writeUTF("Client connected via address: " + socket.getInetAddress().getHostAddress());
                }
            }

            this.serverSocket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAllClients(String text) {
        for (ServerClient client : clients) {
            client.writeUTF(text);
        }
        System.out.println(text);
    }


    public void writeStringToSocket(Socket socket, String text) {

        try {
            socket.getOutputStream().write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(ServerClient serverClient) {
        String nickname = serverClient.getName();
        this.clients.remove(serverClient);

        Thread t = this.clientThreads.get(nickname);
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.clientThreads.remove(nickname);

        System.out.println("Connected clients: " + this.clients.size());
    }
}
