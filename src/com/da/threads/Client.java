package com.da.threads;

import com.da.communication.CommModule;
import com.da.communication.messages.MessageCBCAST;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


public class Client extends Thread {
    public ObjectInputStream ois = null;
    public ObjectOutputStream oos = null;
    public String host = "localhost";
    public int port = 2222;
    public CommModule comm = null;
    public DataInputStream is = null;
    public PrintStream os = null;
    public String name = "Client";
    public DataInputStream inputLine = null;
    Socket clientSocket = null;
    private boolean connected = false;

    // vector time stamp
    public Client() {
        this.setDaemon(true);
        this.start();
    }

    // Constructor: Host Name, Port Number
    public Client(String ip, int port) {
        this.host = ip;
        this.port = port;
        this.setDaemon(true);
        this.start();
    }

    // Constructor with Client Name, Host Name, Port Number
    public Client(String name, String ip, int port) {
        this(ip, port);
        this.name = name;
    }

    // Setter for Port Number
    public void setPort(int port) {
        this.port = port;
    }

    // Setter for Host Name (IP Address)
    public void setHost(String ip) {
        this.host = ip;
    }

    // Connect to the other Servers 
    public boolean connect() {
        try {
            clientSocket = new Socket(host, port);
            return true;
        } catch (IOException e) {
            connect();
        }
        return false;
    }

    // Send Messages to other Connections
    public void send(MessageCBCAST msg) {
        // time stamp the message on send
        try {
            System.out.println("write to stream:" + msg.toString());
            oos.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.setName("Client");
        try {
            System.out.println("Client Connecting to " + host + ":" + port + "...");
            while (!connected) {
                try {
                    clientSocket = new Socket(host, port);
                    connected = true;
                } catch (IOException e) {
                    // System.out.println("Trying again...");
                    connected = false;
                }
            }
            System.out.println("Connected to " + host + ":" + port + "!");
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            os = new PrintStream(clientSocket.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (clientSocket != null && os != null) {
            os.println(CommModule.MachineNumber);
            System.out.println("My Machine is " + CommModule.MachineNumber + " and has been sent to " + host + ":" + port + "!");
        }
    }

    public boolean isConnected() {
        return connected;
    }

    @SuppressWarnings("deprecation")
    public void close() {
        /*
         * Close the output stream, close the input stream, close the socket.
         */
        os.close();
        try {
            is.close();
            oos.close();
            ois.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.stop();
    }
}
