package com.da.threads;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import com.da.communication.CommModule;
import com.da.communication.messages.Message;
import com.da.communication.messages.MessageCBCAST;
import com.da.communication.messages.MessageFactory;
import com.da.communication.messages.Operation;
import com.da.gui.GUI;
import com.da.types.OType;

public class ClientThread extends Thread {
    // clientThread is the connected machine to THIS machine

    // client id for the thread (the machine with the clientId id, that sends the message to THIS machine)
    public int clientId;

    // the input stream for receiving messages
    public DataInputStream is = null;

    // the input stream for reading Protocol Messages from the connected machinea
    public ObjectInputStream ois = null;

    // the output stream for writing Protocol Messages to the connected machine
    public ObjectOutputStream oos = null;

    // the output stream for writing messages to the connected machine
    public PrintStream os = null;

    // the maximum clients connected
    public int maxClientsCount = -1;

    // the vector of clients connected, not
    public ClientThread[] threads = null;

    public Socket clientSocket = null;

    private boolean isConnected = false;

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.setName("ClientThread on Socket: " + clientSocket.getInetAddress());
        this.clientSocket = clientSocket;
        this.threads = threads;
        for (int i = 0; i < maxClientsCount; i++) {
            if (threads[i] == this) {
                this.clientId = i;
                break;
            }
        }

        System.out.println("My client id is: " + this.clientId);
        try {
            // create input stream for the connection
            is = new DataInputStream(clientSocket.getInputStream());
            // create Protocol Message Input Stream for the connection
            ois = new ObjectInputStream(clientSocket.getInputStream());
            // create output stream for the connection
            os = new PrintStream(clientSocket.getOutputStream());
            // create Protocol Message Output Stream for the connection
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            // set connection status in communication module
        } catch (IOException e) {
            e.printStackTrace();
        }
        maxClientsCount = threads.length;

    }

    public void executeOperation(MessageCBCAST msg) {
        int pos;
        String chr = "";

        switch (msg.operation.type) {
            case INSERT:
                chr = "" + msg.operation.character;
                pos = msg.operation.position;
                GUI.messages.insert(chr, pos);
                break;
            case DELETE:
                pos = msg.operation.position;
                GUI.messages.replaceRange("", pos, pos + 1);
                break;
        }
    }

    public boolean isConnected(){
        return isConnected;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        try {

            // Client Machine Number
            String number = is.readLine();
            this.clientId = Integer.parseInt(number);
            System.out.println("client with number " + clientId + " has connected");
            isConnected = true;
            try {
                // read the right type of message from the stream based on the protocol used in CommModule
                Message cbmsg;

                // Read First Message
                cbmsg = MessageFactory.createMessage(ois.readObject(), CommModule.type);

                // set the stop reading operation
                Operation stop = new Operation(OType.INSERT, 'q', 0);

                // read messages until stop is received
                while (!cbmsg.operation.equals(stop)) {

                    // Process Message
                    CommModule.processMessage(clientId, cbmsg);

                    // Read Message
                    cbmsg = MessageFactory.createMessage(ois.readObject(), CommModule.type);
                    System.out.println("ClientThread receive: " + cbmsg.toString());
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            is.close();
            ois.close();
            os.close();
            oos.close();
            this.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}