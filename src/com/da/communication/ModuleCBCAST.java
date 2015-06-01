package com.da.communication;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.da.communication.messages.Message;
import com.da.communication.messages.MessageCBCAST;
import com.da.communication.messages.Operation;
import com.da.gui.GUI;
import com.da.utils.VTComparator;

public class ModuleCBCAST extends Thread implements Module{
	
	// Vector TimeStamp 
	public static volatile ArrayList<Integer> vt = new ArrayList<Integer>(CommModule.CLIENTS + 1);
	
	// Message Queue
	public static volatile ArrayList<MessageCBCAST> msgQueue = new ArrayList<MessageCBCAST>();
	
	// Constructor
	public ModuleCBCAST() {
		this.setName("CBCAST Algorithm");
		for (int i = 0; i < CommModule.CLIENTS; i ++){
			vt.add(0);
		}
	}
	
	// Constructor with Name Parameter
	public ModuleCBCAST(String name) {
		this();
		this.setName(name);
	}
	
	// Send message method
	public synchronized void send(Message m){
		MessageCBCAST msg = (MessageCBCAST) m;
		Integer x = vt.get(CommModule.MachineNumber) + 1;
		vt.set(CommModule.MachineNumber, x);
		msg.setVT(vt);
		msg.timestamp();
		for (int i = 0; i < CommModule.connections.size(); i++) {
			try {
				CommModule.connections.get(i).send(msg);
			} catch (NullPointerException e) {
				System.out.println("sendind a message to a non-existant (non-connected) connection "+i);
			}
		}
	}

	public synchronized void updateVT(ArrayList<Integer> cvt){
		for (int i = 0; i < cvt.size(); i ++) {
			vt.set(i, Math.max(cvt.get(i),vt.get(i)));
		}
	}
	public boolean needsDelay(int client, MessageCBCAST m) {
		ArrayList<Integer> timestamp = m.vt;
		if (timestamp.get(client) != (ModuleCBCAST.vt.get(client) + 1) && client != CommModule.MachineNumber) {
			return true;
		}

		for (int i = 0; i < timestamp.size(); i++) {
			if (i != client && client != CommModule.MachineNumber) {
				if (timestamp.get(i) > vt.get(i)) {
					return true;
				}
			}
		}

		return false;
	}

	public void deliver(MessageCBCAST m) {
	}

	// Message Processing Function with Queue Ordering and Delay
	public synchronized void processMessage(int clientId, Message m){
		MessageCBCAST msg = (MessageCBCAST) m;
		// primesc msg
		// vad daca poate fi livrat
		if (needsDelay(clientId, msg)) {
			ModuleCBCAST.msgQueue.add(msg);
			Collections.sort(ModuleCBCAST.msgQueue, new VTComparator());
		} else {
			// update client's vector timestamp
			updateVT(msg.vt);
			// execute operation on GUI
			executeOperation(msg.operation);
		}

		boolean atLeastOneDelivered = true;
		while(atLeastOneDelivered) {
			atLeastOneDelivered = false;
			ArrayList<Message> temp = new ArrayList<Message>();
			for (Message message : ModuleCBCAST.msgQueue) {
				MessageCBCAST cbcastMessageTemp = (MessageCBCAST) message;
				if (!needsDelay(clientId, cbcastMessageTemp)) {
					temp.add(message);
					// update client's vector timestamp
					updateVT(msg.vt);
					// execute operation on GUI
					executeOperation(msg.operation);
					atLeastOneDelivered = true;
				}
			}

			for (Message message : temp) {
				ModuleCBCAST.msgQueue.remove(message);
				Collections.sort(ModuleCBCAST.msgQueue, new VTComparator());
			}
		}
	}

	// Execute Operation From Message on GUI Text Area
	public synchronized void executeOperation(Operation op){
		int pos;
		String chr = "";

		switch (op.type){
			case INSERT:
				chr = "" + op.character;
				pos = op.position;
				if (pos <= GUI.messages.getText().length()){
					GUI.insertByCode = true;
					GUI.messages.insert(chr, pos);
				}else{
					System.out.println("invalid INSERT operation for the system");
				}

				GUI.insertByCode = false;
				break;
			case DELETE:
				pos = op.position;
				if (pos+1 < GUI.messages.getText().length()){
					GUI.insertByCode = true;
					GUI.messages.replaceRange("", pos, pos+1);
				}else {
					System.out.println("invalid DELETE operation for the system");
				}
				GUI.insertByCode = false;
				break;
		}
	}
	public void run() {}
}
