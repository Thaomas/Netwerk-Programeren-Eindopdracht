package server;

import client.Client;
import client.ClientGUI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import static javafx.application.Application.launch;

public class User {

    private String hostname;
    private int port;
    private boolean isConnected = false;
    private Socket socket;
    private ClientGUI gui;
    private ArrayList<String> chatLog;

    public static void main(String[] args) {
        Client client = new Client();
        launch(ClientGUI.class);
        client.connect();
    }

    public User() {
        Scanner scanner = new Scanner(System.in);
        while (!isConnected) {
            System.out.println("Enter server ip:");
            this.hostname = scanner.nextLine();
            System.out.println("Enter port");
            this.port = scanner.nextInt();
            try {
                this.socket = new Socket(this.hostname, this.port);
                connect();
                isConnected = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("Invalid port or ip");
                e.printStackTrace();
            }
        }
        chatLog = new ArrayList<>();
    }

    public void connect() {
        System.out.println("Connecting to server: " + this.hostname + " on port " + this.port);

        Scanner scanner = new Scanner(System.in);

        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());


            System.out.print("Enter a nickname: ");
            String nickName = scanner.nextLine();
            out.writeUTF(nickName);

            System.out.println("You are now connected as " + nickName);

            String input = "";

            //boolean isRunning = true;

            Thread readSocketThread = new Thread(() -> receiveDataFromSocket(in));

            readSocketThread.start();

            while (!input.equals("\\quit")) {
                //System.out.print("(" +nickName + "): ");
                input = scanner.nextLine();
                out.writeUTF(input);
                //System.out.print("Sended: " + input);
            }
            isConnected = false;

            socket.close();
            try {
                readSocketThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void receiveDataFromSocket(DataInputStream in) {
        String received;
        while (isConnected) {
            try {
                received = in.readUTF();
                System.out.println(received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void writeStringToSocket(Socket socket, String text) {

        try {
            socket.getOutputStream().write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

