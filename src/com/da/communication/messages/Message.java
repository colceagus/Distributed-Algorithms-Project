package com.da.communication.messages;


public class Message implements java.io.Serializable{
	private static final long serialVersionUID = -163710166775293056L;
	
	// The Operation to be made on the text editor
	public Operation operation;
	public int senderId = -1;
	
	public Message(){
		setOperation(new Operation());
	}
	
	public Message(Operation op) {
		setOperation(op);
	}
	
	public void setOperation(Operation op) {
		this.operation = new Operation(op.type,op.character,op.position);
	}
	
	public Operation getOperation() {
		return this.operation;
	}
	
	public boolean equals(Message m) {
		return m.operation.equals(this.operation);
	}
	
	public void setSender(int a) {
		this.senderId = a;
	}
	public int getSender() {
		return this.senderId;
	}
}
