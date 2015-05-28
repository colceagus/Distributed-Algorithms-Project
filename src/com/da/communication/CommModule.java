package com.da.communication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.da.communication.messages.Message;
import com.da.communication.messages.MessageFactory;
import com.da.communication.messages.Operation;
import com.da.gui.GUI;
import com.da.threads.Client;
import com.da.threads.ClientThread;
import com.da.types.OType;
import com.da.types.Protocol;

public class CommModule extends Thread {
    //
    // Server Parameters
    // ---------------------------------------
    public static String address = "localhost";
    public static int port = 2222;
    public static ServerSocket serverSocket = null;
    public static int MachineNumber = 0;

    // Connections and Threads
    public static int CLIENTS = 3;
    public static ArrayList<String[]> clientParams = new ArrayList<String[]>();
    public static boolean[] connectionsStatus = new boolean[CLIENTS];
    public static ClientThread[] threads = new ClientThread[CLIENTS];
    public static ArrayList<Client> connections = new ArrayList<Client>();

    // Protocol
    public static Protocol type;

    // Configuration
    public static String configFile;

    // public static CommModule module ;
    private static Socket clientSocket = null;

    // Communication
    // instance of the Protocol Module
    public static Module commModule = null;

    // "Factory" Function
    public void createComm() {
        switch (type) {
            case CBCAST:
                commModule = new ModuleCBCAST();
                break;
            default:
                commModule = new ModuleCBCAST();
                break;
        }
    }

    // Constructor
    public CommModule(String configFile, Protocol type) {
        this.setName(type.toString());
        this.setConfigFile(configFile);
        this.setProtocol(type);
        this.listen();
    }

    // Protocol(Algorithm) Type Setter
    public void setProtocol(Protocol prot) {
        CommModule.type = prot;
        this.setName(prot.toString());
    }

    // Configuration File Setter
    public void setConfigFile(String config) {
        CommModule.configFile = config;
    }

    // Parser And Processor Function for Configuration File
    public void parseConfig(String configFile) {
        FileInputStream fstream;
        try {

            fstream = new FileInputStream(configFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // first read number of clients
            String strLine = br.readLine();
            CLIENTS = Integer.parseInt(strLine);
            System.out.println("Number of clients: " + CLIENTS);

            // set connection Status Vector
            connectionsStatus = new boolean[CLIENTS];

            // read server address, server port, machine number
            strLine = br.readLine();
            String[] params = strLine.split(" ");

            // Server Parameters
            address = params[0];
            port = Integer.parseInt(params[1]);
            MachineNumber = Integer.parseInt(params[2]);
            setConnectionStatus(MachineNumber);
            // Create Client Parameters for further processing
            // for one connection only now
            for (int i = 0; i < CLIENTS - 1; i++) {
                strLine = br.readLine();
                params = strLine.split(" ");
                clientParams.add(params);
            }

            // Create Communication Module based on Protocol(Algorithm) Type
            createComm();

            br.close();
            in.close();
            fstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Port Number Setter
    public void setPort(int port) {
        CommModule.port = port;
    }

    // Machine Number Setter
    public void setNumber(int number) {
        CommModule.MachineNumber = number;
    }

    // set client id connection flag to true
    public static void setConnectionStatus(int clientIndex) {
        // set a flag for every successful connection
        try {
            connectionsStatus[clientIndex] = true;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("connection status index error");
            e.printStackTrace();
        }
        // verify for all clients connection
        // then read Driver File, Execute operations to Send Messages to Other
        // Connected Clients
        int i = 0;
        for (; i < connectionsStatus.length && connectionsStatus[i]; i++) ;
        if (i == connectionsStatus.length) {
            readDriverFile();
        }
    }

    // Listen for Incoming Connections
    public void listen() {
        boolean socketConnected = false;
        while (!socketConnected) {
            try {
                // open a new socket to listen for connections to this machine
                serverSocket = new ServerSocket(port);
                socketConnected = true;
            } catch (IOException e) {
                System.out.println("Server could not connect to port "+port+", trying on "+(port+1));
                port++;
            }
        }

        Thread listen = new Thread() {
            @Override
            public void run() {

                // we will listen indefinitely for incoming connections
                while (true) {
                    try {
                        // listen for a upcoming connection, accept that
                        // connection
                        clientSocket = serverSocket.accept();
                        System.out.println("new connection accepted.");
                        // look for available threads for processing that
                        // connection
                        for (int i = 0; i < CLIENTS; i++) {

                            // find a new unallocated thread for the new
                            // communication to be processed
                            if (threads[i] == null) {
                                (threads[i] = new ClientThread(clientSocket, threads)).start();
                                for (int j = 0; j < CLIENTS; i++)
                                    if (j != i) {
                                        threads[j].threads = threads;
                                    }
                                break;
                            }

                            // if maximum connection number is met, refuse the
                            // new connection.
                            if (i == CLIENTS) {
                                PrintStream os = new PrintStream(clientSocket.getOutputStream());
                                os.println("Server Full. Try again.");
                                os.close();
                                clientSocket.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        listen.start();
    }

    // Connect to the Other Machines
    public void connect() {
        Thread connect = new Thread("Connections Thread") {
            @Override
            public void run() {
                // implement connections with timeout
                try {
                    for (int i = 0; i < clientParams.size(); i++) {
                        System.out.println(clientParams.get(i)[0]+":"+clientParams.get(i)[1]);
                        connections.add(new Client(clientParams.get(i)[0], Integer
                                .parseInt(clientParams.get(i)[1])));
                    }

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        };
        connect.start();
    }

    // Message Sender Function
    public static void send(Message msg) {
        commModule.send(msg);
    }

    // Process Incoming Message
    public static void processMessage(int clientId, Message msg) {
        commModule.processMessage(clientId, msg);
    }

    // Execute Operation from Message onto GUI
    public static void executeOperation(Operation op) {
        commModule.executeOperation(op);
    }

    // Driver File Processor
    public static void readDriverFile() {
        FileInputStream fis;
        try {
            fis = new FileInputStream("driver.txt");
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // do Reading
            String line = br.readLine();
            while (line != null) {
                // read Operation from File
                String op = line.substring(0, 3);
                int pos = -1;
                Operation operation = null;
                Message msg = null;

                // build new Operation for Message Sending
                switch (op) {

                    case "ins":
                        String[] split = line.split("'");
                        String chr = split[1];
                        pos = Integer.parseInt(split[2].substring(
                                split[2].indexOf(",") + 1, split[2].indexOf(")")));
                        operation = new Operation(OType.INSERT, chr.charAt(0), pos);
                        msg = MessageFactory.provideMessage(CommModule.type,
                                operation);
                        break;
                    case "del":
                        pos = Integer.parseInt(line.substring(
                                line.indexOf("(") + 1, line.indexOf(")")));
                        operation = new Operation(OType.DELETE, pos);
                        msg = MessageFactory.provideMessage(CommModule.type,
                                operation);
                        break;
                }
                // send message to other parties
                send(msg);
                // execute message on GUI
                executeOperation(operation);

                line = br.readLine();
            }
            br.close();
            in.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Thread Run Method
    public void run() {

        connect();

    }
}
