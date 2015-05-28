package com.da.threads;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.da.communication.CommModule;
import com.da.communication.messages.MessageCBCAST;


public class Client extends Thread{
	Socket clientSocket = null;
	public ObjectInputStream ois = null;
    public ObjectOutputStream oos = null;
    public String host = "localhost";
    public int port = 2222;
	public CommModule comm = null;
	public DataInputStream is = null;
	public PrintStream os = null; 
	public String name = "Client";
    public DataInputStream inputLine = null;
    
    // vector time stamp
    public Client(){
    	this.setDaemon(true);
    	this.start();
    }
    
    // Constructor: Host Name, Port Number
    public Client(String ip,int port){
    	this.host = ip;
    	this.port = port;
    	this.setDaemon(true);
    	this.start();
    }
    
    // Constructor with Client Name, Host Name, Port Number
    public Client(String name,String ip,int port){
    	this(ip,port);
    	this.name = name;
    } 
    
    // Setter for Port Number
    public void setPort(int port){
    	this.port = port;
    }
    
    // Setter for Host Name (IP Address)
    public void setHost(String ip){
    	this.host = ip;
    }
    
    // Connect to the other Servers 
    public boolean connect() {
		try {
			clientSocket = new Socket(host,port);
			return true;
		} catch (IOException e) {
			connect();
		}
		return false;
    }
    
    // Send Messages to other Connections
    public void send(MessageCBCAST msg){
		// time stamp the message on send
		try {
			System.out.println("write to stream:" + msg.toString());
			oos.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
	public void run(){
		this.setName("Client");
		try {
			boolean connected = false;
			System.out.println("Connecting...");
			while (!connected){
				try {
					clientSocket = new Socket(host,port);
					connected = true;
				} catch (IOException e){
					// System.out.println("Trying again...");
					connected = false;
				}
			}
			System.out.println("Connected to "+port+"!");
			oos = new ObjectOutputStream(clientSocket.getOutputStream());
			os = new PrintStream(clientSocket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (clientSocket != null && os != null ) { //&& is != null
		        System.out.println("The client started. Type any text. To quit it type 'q' in 1st position in the text area.");
		        os.println(CommModule.MachineNumber);
		        System.out.println("My number is :"+CommModule.MachineNumber);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void close(){
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
