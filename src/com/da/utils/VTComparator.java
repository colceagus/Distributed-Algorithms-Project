package com.da.utils;

import com.da.communication.messages.MessageCBCAST;

import java.util.ArrayList;
import java.util.Comparator;


public class VTComparator implements Comparator<MessageCBCAST> {

    public int compare(MessageCBCAST m1, MessageCBCAST m2) {
        ArrayList<Integer> vt1 = m1.vt;
        ArrayList<Integer> vt2 = m2.vt;

        for (int i = 0; i < vt1.size(); i++) {
            if (vt1.get(i) >= vt2.get(i)) {
                return 1;
            }
        }
        return -1;
    }

}