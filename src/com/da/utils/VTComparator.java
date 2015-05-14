package com.da.utils;
import java.util.ArrayList;
import java.util.Comparator;

import com.da.communication.messages.Message;
import com.da.communication.messages.MessageCBCAST;


public class VTComparator implements Comparator<MessageCBCAST>{
	
	//@Override
	 public int compare(MessageCBCAST m1, MessageCBCAST m2) {
		
		ArrayList<Integer> vt1 = m1.vt; 
		ArrayList<Integer> vt2 = m2.vt;

		int[] comparison  = new int[vt1.size()];
		boolean isGreater = false;
		boolean isOne = false, isMinusOne = false;
		// vezi ca sunt elemente mai mici si mai mari => vt sunt egali => compar id-urile site-urilor
		for (int i = 0; i < vt1.size(); i ++) {
			comparison[i] = vt1.get(i) - vt2.get(i);
			if (comparison[i] >= 0)
				isGreater = true;
			if (comparison[i] >= 0)
				isOne = true;
			if (comparison[i] < 0) 
				isMinusOne = true;
			System.out.print(comparison[i]+" ");
		}
		System.out.println();
		if (isOne && isMinusOne)
			return (m1.senderId > m2.senderId) ? 1 : -1;
		
		return (isGreater == true) ? 1 : -1;
	}
    /*
    @Override
    public int compare(MessageCBCAST m1, MessageCBCAST m2) {
        boolean areEq = true;

        ArrayList<Integer> vt1 = m1.vt;
        ArrayList<Integer> vt2 = m2.vt;

        for (int i = 0; i < vt1.size(); i ++  ) {
            if (vt1.get(i) > vt2.get(i))
                return 1;
            if (vt1.get(i) < vt2.get(i))
                areEq = false;
        }

        if (areEq)
            return 0;

        return -1;
    }*/
}