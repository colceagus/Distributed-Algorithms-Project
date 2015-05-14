package com.da.communication.messages;

import com.da.types.OType;

public class Operation implements java.io.Serializable {
	private static final long serialVersionUID = -7008294687780811965L;
	
	public OType type;
	public int position;
	public char character;
	
	public Operation() {
		this.type = OType.INSERT;
		this.position = 1;
		this.character = 'a';
	}
	
	public Operation(OType t, int pos){
		this.type = t;
		this.position = pos;
		this.character = 'a';
	}
	
	public Operation(OType t, char a, int pos){
		this(t,pos);
		this.character = a;
	}
	
	@Override 
	public String toString(){
		if (this.type == OType.INSERT){
			return this.type + "('" + this.character + "'," + this.position +")";
		}
		return this.type + "(" + this.position + ")";
	}
	
	public boolean equals(Operation o){
		if (this.type == o.type && this.type == OType.DELETE){
			return this.position == o.position; 
		}
		
		return (o.type.equals(this.type)) && (this.position == o.position) && (this.character == o.character);
	}
}
