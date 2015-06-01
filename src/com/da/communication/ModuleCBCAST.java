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
	public void send(Message m){
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
	
	public void updateVT(ArrayList<Integer> cvt){
		for (int i = 0; i < cvt.size(); i ++) {
			if (cvt.get(i) > vt.get(i)){
				vt.set(i, cvt.get(i));
			}
		}
	}

	// Message Processing Function with Queue Ordering and Delay
	public void processMessage(int clientId, Message m){
		MessageCBCAST msg = (MessageCBCAST) m;
		// primesc msg
		// vad daca poate fi livrat
		
		boolean deliver = true;
		// prima conditie
		if (ModuleCBCAST.vt.get(clientId) + 1 == msg.vt.get(clientId)){
			// a doua conditie de livrare
			for (int i = 0; i < msg.vt.size() && deliver; i ++) {
				
				if (i != clientId)
				if (msg.vt.get(i) <= ModuleCBCAST.vt.get(i))
					deliver = true;
				else 
					deliver = false;
			}
		}
		if (deliver) {
			// update client's vector timestamp
			updateVT(msg.vt);
			//ModuleCBCAST.vt.set(clientId,msg.vt.get(clientId));
			
			// execute operation on GUI
			executeOperation(msg.operation);
			
			// after execution check again for all messages delivery
		}else{
			// daca nu, adaug in queue, sortez, verific din nou pentru queue[0]
			ModuleCBCAST.msgQueue.add(msg);
			List<MessageCBCAST> queue = new ArrayList<MessageCBCAST>(ModuleCBCAST.msgQueue);
			
			for (int i=0;i<queue.size();i++) { 
				System.out.println(queue.get(i)+" ");
			}
			
			queue.add(msg);
			Collections.sort(queue, new VTComparator());
			// sortez coada
			System.out.println();
			System.out.print("Queue: ");
			for (int i=0;i<queue.size();i++) { 
				System.out.print(queue.get(i)+" ");
			}
			System.out.println();
			// pentru a livra verific primul mesaj din coada si compar vt din mesaj cu current vt al site-ului
			
			deliver = true;
			msg = queue.get(0);
			// prima conditie
			if (ModuleCBCAST.vt.get(clientId) + 1 == msg.vt.get(clientId)){
				// a doua conditie de livrare
				for (int i = 0; i < msg.vt.size() && deliver; i ++) {
					
					if (i != clientId)
					if (msg.vt.get(i) <= ModuleCBCAST.vt.get(i))
						deliver = true;
					else 
						deliver = false;
				}
			}
			if (deliver) {
				// update client's vector timestamp
				updateVT(msg.vt);
				//ModuleCBCAST.vt.set(clientId,msg.vt.get(clientId));
				
				// execute operation on GUI
				executeOperation(queue.remove(0).operation);
				
				// after execution check again for all messages delivery
			}
		}
	}

	// Execute Operation From Message on GUI Text Area
	public void executeOperation(Operation op){
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
	public void run() {
	}
}
