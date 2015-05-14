package com.da.communication;

import com.da.communication.messages.Message;
import com.da.communication.messages.Operation;

public interface Module {
	public void send(Message msg);
	public void processMessage(int clientId, Message msg);
	public void executeOperation(Operation op);
}
