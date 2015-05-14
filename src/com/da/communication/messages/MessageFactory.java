package com.da.communication.messages;
import com.da.communication.CommModule;
import com.da.types.Protocol;


public class MessageFactory {
	
	public static Message provideMessage(Protocol type, Operation op){
		Message retmsg;
		switch (type){
		case CBCAST:
			retmsg = new MessageCBCAST(op);
			retmsg.senderId = CommModule.MachineNumber;
			break;
		default: 
			retmsg = new MessageCBCAST(op);	
			retmsg.senderId = CommModule.MachineNumber;
			break;
		}
		return retmsg;
	}
	
	public static Message createMessage(Object msg,Protocol type){
		Message retmsg;
		switch (type){
		case CBCAST:
			retmsg = (MessageCBCAST) msg;
			retmsg.senderId = CommModule.MachineNumber;
			break;
		default:
			retmsg = (MessageCBCAST) msg;
			retmsg.senderId = CommModule.MachineNumber;
			break;
		}
		return retmsg;
	}
}
