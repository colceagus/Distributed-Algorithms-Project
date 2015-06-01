package com.da.gui;

import com.da.communication.CommModule;
import com.da.communication.messages.MessageCBCAST;
import com.da.communication.messages.Operation;
import com.da.types.OType;
import com.da.types.Protocol;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

public class GUI extends JFrame {

    public static final CommModule commModule = new CommModule();
    /**
     *
     */
    private static final long serialVersionUID = 4994280587429433917L;
    public static int WIDTH = 600;
    public static int HEIGHT = 400;
    public static GUI window = null;
    public static JLabel configFileLabel;
    public static JTextArea messages;
    public static JButton startServer;
    public static Container pane;
    public static Protocol protocol = Protocol.CBCAST;
    public static boolean insertByCode = false;
    public String message = "";
    public int CLIENTS = 1;

    public GUI() {
        this.createGraphics();

        commModule.bootUp(protocol);

        final Document document = messages.getDocument();

        final DocumentListener dlistener = new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (!insertByCode) {
                    MessageCBCAST msg = new MessageCBCAST(new Operation(OType.DELETE, e.getOffset()));
                    CommModule.send(msg);
                }
                updateLog(e, OType.DELETE);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!insertByCode) {
                    String xx;
                    try {
                        xx = e.getDocument().getText(e.getOffset(), 1);
                        char x = xx.charAt(0);
                        MessageCBCAST msg = new MessageCBCAST(new Operation(OType.INSERT, x, e.getOffset()));
                        CommModule.send(msg);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
                updateLog(e, OType.INSERT);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            public void updateLog(DocumentEvent e, OType action) {
                int changeLength = e.getLength();

                String character = "";
                try {
                    character = e.getDocument().getText(e.getOffset(), 1);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }

                System.out.println(changeLength + " character"
                        + ((changeLength == 1) ? " " : "s ") + action + " "
                        + character + " " + e.getOffset() + ".");
            }

        };
        document.addDocumentListener(dlistener);

        // Set the JFrame Width, Height, Close Operation
        setSize(WIDTH, HEIGHT);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void createGraphics() {
        setTitle("GUI for MultiThreaded ClientServer Application");

        //
        // Instantiate Labels
        // ------------------------------------

        // Configuration File
        configFileLabel = new JLabel("Configuration File: ", SwingConstants.LEFT);

        // Messages Text Field
        messages = new JTextArea(20, 35);
        messages.setWrapStyleWord(true);
        messages.setLineWrap(true);
        String fill = " ";
        for (int i = 0; i < 20 * 35; i++) {
            fill += " ";
        }
        messages.setText(fill);

        // Create Buttons
        // ------------------------------------

        // Button for starting the commModule
        startServer = new JButton("Start Server");

        //
        // Content Pane
        // ------------------------------------

        // Get content pane for the frame
        pane = getContentPane();

        GroupLayout layout = new GroupLayout(pane);
        pane.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout
                        .createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup().addComponent(configFileLabel))
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(messages))
        );

        layout.setVerticalGroup(layout
                        .createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE)
                                        .addComponent(configFileLabel))
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE).addComponent(
                                        messages))
        );
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String number = args[0];
            CommModule.MachineNumber = Integer.parseInt(number);
            if (args[1] != null) {
                try {
                    protocol = Protocol.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException | NullPointerException e) {
                    e.printStackTrace();
                    protocol = Protocol.CBCAST;
                }
            }
            window = new GUI();

            configFileLabel.setText(protocol + " Machine " + (CommModule.MachineNumber + 1));
            GUI.commModule.start();

        } else {
            window = new GUI();

        }
    }


}
