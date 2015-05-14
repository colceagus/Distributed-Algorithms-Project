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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    /**
     *
     */
    private static final long serialVersionUID = 4994280587429433917L;
    public static int WIDTH = 600;
    public static int HEIGHT = 400;
    public static GUI window = null;


    public static JLabel configFileLabel, algorithmLabel, serverAddressLabel, serverPortLabel,
            client1AddressLabel, client1PortLabel, client2AddressLabel,
            client2PortLabel, insertLabel, deleteLabel, charLabel, insPosLabel,
            delPosLabel, machineNumberLabel;

    public static JComboBox<Protocol> algorithmCombo;
    public static JComboBox<String> configFileCombo;

    public static JTextField serverAddress, serverPort, client1Address,
            client2Address, client1Port, client2Port, charTF, insPosTF,
            delPosTF, machineNumber;
    public static JTextArea messages;
    public static JButton startServer, connectClients, insButton, delButton;
    public static Container pane;
    public static String configFile = "machine1.txt";
    public static Protocol protocol = Protocol.CBCAST;
    public static boolean insertByCode = false;
    public String message = "";
    public int CLIENTS = 1;

    public GUI() {
    }

    public GUI(int machineNumber, String configFile) {
        setTitle("GUI for MultiThreaded ClientServer Application");

        //
        // Instantiate Labels
        // ------------------------------------

        // Configuration File
        configFileLabel = new JLabel("Configuration File: ", SwingConstants.LEFT);
        String[] files = {"machine1.txt", "machine2.txt", "machine3.txt"};
        configFileCombo = new JComboBox<String>(files);
        configFileCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<String> temp = (JComboBox<String>) e.getSource();
                configFile = (String) temp.getSelectedItem();
            }
        });

        // Protocol
        algorithmLabel = new JLabel("Algorithm: ", SwingConstants.LEFT);
        algorithmCombo = new JComboBox<Protocol>(Protocol.values());
        algorithmCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
                JComboBox<Protocol> temp = (JComboBox<Protocol>) e.getSource();
                protocol = (Protocol) temp.getSelectedItem();
            }
        });

        // The Machine
        serverAddressLabel = new JLabel("Server Address: ", SwingConstants.LEFT);
        serverPortLabel = new JLabel("Port Address: ", SwingConstants.LEFT);
        machineNumberLabel = new JLabel("Machine Number: ", SwingConstants.LEFT);

        // First Client
        client1AddressLabel = new JLabel("1st Client Address: ",
                SwingConstants.LEFT);
        client1PortLabel = new JLabel("1st Client Port", SwingConstants.LEFT);

        // Second Client
        client2AddressLabel = new JLabel("2nd Client Address: ",
                SwingConstants.LEFT);
        client2PortLabel = new JLabel("2nd Client Port", SwingConstants.LEFT);

        // Insert Label
        insertLabel = new JLabel("Insert ");

        // Character Label
        charLabel = new JLabel("Char ");

        // Position Label
        insPosLabel = new JLabel("Pos ");
        delPosLabel = new JLabel("Pos ");

        // Delete Label
        deleteLabel = new JLabel("Delete ");

        //
        // Instantiate Text fields
        // ------------------------------------
        Dimension minSmallDim = new Dimension(35, 20);
        Dimension maxSmallDim = new Dimension(50, 20);
        Dimension bigDim = new Dimension(100, 20);
        // The Machine
        serverAddress = new JTextField(15);
        serverAddress.setMaximumSize(bigDim);
        serverPort = new JTextField(4);
        serverPort.setMaximumSize(maxSmallDim);
        serverPort.setMinimumSize(minSmallDim);
        machineNumber = new JTextField(20);
        machineNumber.setMaximumSize(maxSmallDim);

        // First Client
        client1Address = new JTextField(15);
        client1Address.setMaximumSize(bigDim);
        client1Port = new JTextField(4);
        client1Port.setMaximumSize(maxSmallDim);

        // Second Client
        client2Address = new JTextField(15);
        client2Address.setMaximumSize(bigDim);
        client2Port = new JTextField(4);
        client2Port.setMaximumSize(maxSmallDim);

        // Insert Action
        charTF = new JTextField("a");
        charTF.setMaximumSize(maxSmallDim);
        insPosTF = new JTextField("2");
        insPosTF.setMaximumSize(maxSmallDim);

        // Delete Action
        delPosTF = new JTextField();
        delPosTF.setMaximumSize(maxSmallDim);

        // Messages Text Field
        messages = new JTextArea(20, 35);
        messages.setWrapStyleWord(true);
        messages.setLineWrap(true);
        String fill = " ";
        for (int i = 0; i < 20 * 35; i++) {
            fill += " ";
        }
        messages.setText(fill);
        //
        // Create Buttons
        // ------------------------------------

        // Button for starting the commModule
        startServer = new JButton("Start Server");

        // Button for connecting to the other servers
        connectClients = new JButton("Connect Clients");

        // Insert Action Button
        insButton = new JButton("Insert");

        // Delete Action Button
        delButton = new JButton("Delete");

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
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(configFileLabel)
                                        .addComponent(configFileCombo))
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(algorithmLabel)
                                        .addComponent(algorithmCombo))

                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(startServer))
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(messages))
        );

        layout.setVerticalGroup(layout
                        .createSequentialGroup()
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE)
                                        .addComponent(configFileLabel)
                                        .addComponent(configFileCombo))
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE)
                                        .addComponent(algorithmLabel)
                                        .addComponent(algorithmCombo))
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE).addComponent(
                                        startServer))
                        .addGroup(
                                layout.createParallelGroup(
                                        GroupLayout.Alignment.BASELINE).addComponent(
                                        messages))
        );

        // Set Default TextFields' text
        serverAddress.setText("localhost");
        serverPort.setText("2222");
        machineNumber.setText("0");

        client1Address.setText("localhost");
        client1Port.setText("2223");

        client2Address.setText("localhost");
        client2Port.setText("2224");

        final CommModule commModule = new CommModule(configFile, protocol);

        // Add Buttons Actions
        startServer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                commModule.setConfigFile(configFile);
                commModule.parseConfig(configFile);
                commModule.setProtocol(protocol);
                commModule.start();
                startServer.setEnabled(false);
            }

        });

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

    public static void main(String[] args) {
        window = new GUI();
    }
}
