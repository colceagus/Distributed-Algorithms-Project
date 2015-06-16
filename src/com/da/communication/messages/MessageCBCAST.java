package com.da.communication.messages;

import com.da.communication.CommModule;

import java.util.ArrayList;

public class MessageCBCAST extends Message implements java.io.Serializable {
    private static final long serialVersionUID = -5212804843246099826L;
    // Vector TimeStamp
    public ArrayList<Integer> vt;
    public int vtlen = CommModule.connections.size() + 1;

    public MessageCBCAST() {
        super();
        vtlen = CommModule.connections.size() + 1;
        this.vt = new ArrayList<Integer>(vtlen);
        for (int i = 0; i < vtlen; i++) {
            this.vt.add(0);
        }
    }

    public MessageCBCAST(Operation op) {
        this();
        setOperation(op);
    }

    public MessageCBCAST(Operation op, ArrayList<Integer> timestamp) {
        this(op);
        this.vt = new ArrayList<Integer>(timestamp);
    }

    public void setVT(ArrayList<Integer> timestamp) {
        this.vt = new ArrayList<Integer>(timestamp);
    }

    @Override
    public String toString() {
        String svt = "[" + vt.get(0);

        for (int i = 1; i < vt.size(); i++) {
            svt += "," + vt.get(i).toString();
        }

        svt += "]";
        return "(" + getOperation().toString() + "," + svt + ")";
    }
}
