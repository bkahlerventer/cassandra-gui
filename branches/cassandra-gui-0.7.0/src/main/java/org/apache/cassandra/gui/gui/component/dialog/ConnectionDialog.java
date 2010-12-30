package org.apache.cassandra.gui.gui.component.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.cassandra.gui.client.Client;


public class ConnectionDialog extends JDialog {
    private static final long serialVersionUID = 8707158056959280058L;

    private class EnterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            enterAction();
        }
    }

    private Client client;
    private JButton okButton = new JButton("OK");
    private JTextField hostText = new JTextField();
    private JTextField thriftPortText = new JTextField();
    private JTextField jmxPortTextField = new JTextField();

    public ConnectionDialog(JFrame owner){
        super(owner);
        
        String host = Client.DEFAULT_THRIFT_HOST;
        String thirftPort = String.valueOf(Client.DEFAULT_THRIFT_PORT);	    
	    String jmxProt = String.valueOf(Client.DEFAULT_JMX_PORT);
        
        File sessionFile = new File("session.properties");
        if(sessionFile.exists()) {
        
		    Properties properties = new Properties();
		    try {
		        properties.load(new FileInputStream("session.properties"));
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		    
		    host = properties.getProperty("host");		    
		    thirftPort = properties.getProperty("thriftPort");
		    jmxProt = properties.getProperty("jmxPort");
        }
       
        
//        hostText.setText(Client.DEFAULT_THRIFT_HOST);
        hostText.setText(host);
        thriftPortText.setText(thirftPort);
        jmxPortTextField.setText(jmxProt);

        hostText.addActionListener(new EnterAction());
        thriftPortText.addActionListener(new EnterAction());
        jmxPortTextField.addActionListener(new EnterAction());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Host:"));
        inputPanel.add(hostText);
        inputPanel.add(new JLabel("Port:"));
        inputPanel.add(thriftPortText);
        inputPanel.add(new JLabel("JMX port:"));
        inputPanel.add(jmxPortTextField);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterAction();
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client = null;
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(" "), BorderLayout.NORTH);
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        this.setPreferredSize(new Dimension(300,150));
        pack();
        setModalityType(ModalityType.DOCUMENT_MODAL);
        setTitle("Cassandra GUI");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void enterAction() {
        if (hostText.getText().isEmpty()){
            JOptionPane.showMessageDialog(null, "enter host name.");
            return;
        }

        String host = hostText.getText();
        int thriftPort =
            thriftPortText.getText().isEmpty() ?
                    Client.DEFAULT_THRIFT_PORT :
                    Integer.valueOf(thriftPortText.getText());
        int jmxPort =
            jmxPortTextField.getText().isEmpty() ?
                    Client.DEFAULT_JMX_PORT :
                    Integer.valueOf(jmxPortTextField.getText());

        client = new Client(host, thriftPort, jmxPort);
        
        
        // write setting to a file
        Properties properties = new Properties();
        properties.setProperty("host", host);
        properties.setProperty("thriftPort", Integer.toString(thriftPort));
        properties.setProperty("jmxPort", Integer.toString(jmxPort));
        try {
            properties.store(new FileOutputStream("session.properties"), null);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        try {
            client.connect();
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "connection faild.");
            e1.printStackTrace();
            return;
        }

        setVisible(false);
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }
}

