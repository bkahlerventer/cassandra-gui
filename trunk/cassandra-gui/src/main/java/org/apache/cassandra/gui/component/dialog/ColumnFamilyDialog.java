package org.apache.cassandra.gui.component.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.cassandra.client.Client;
import org.apache.cassandra.client.Client.ColumnType;
import org.apache.cassandra.unit.ColumnFamily;

public class ColumnFamilyDialog extends JDialog {
    private static final long serialVersionUID = -13548946072075769L;

    private class EnterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            enterAction();
        }
    }

    private JTextField columnFamilyText = new JTextField();
    private JComboBox columnTypeBox = new JComboBox();

    private boolean cancel = true;
    private ColumnFamily info = new ColumnFamily();

    public ColumnFamilyDialog(ColumnFamily info) {
        this.info = info;
        create(info);
    }

    public ColumnFamilyDialog() {
        create(null);
    }

    public void create(ColumnFamily selectedInfo) {
        columnFamilyText.addActionListener(new EnterAction());

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Column Family Name: "));
        inputPanel.add(columnFamilyText);

        for (ColumnType ct : Client.ColumnType.values()) {
            columnTypeBox.addItem(ct.toString());
        }
        if (selectedInfo != null) {
            columnTypeBox.setSelectedItem(selectedInfo.getColumnType());
        }
        inputPanel.add(new JLabel("Column Type: "));
        inputPanel.add(columnTypeBox);

        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterAction();
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(ok);
        buttonPanel.add(cancel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        pack();
        setModalityType(ModalityType.DOCUMENT_MODAL);
        setTitle("create or update column family");
        setLocationRelativeTo(null);
        setModal(true);
    }

    private void enterAction() {
        if (columnFamilyText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter Column Family Name.");
            columnFamilyText.requestFocus();
            return;
        }
        info.setColumnFamilyName(columnFamilyText.getText());

//        if (replicationFactorText.getText().isEmpty()) {
//            JOptionPane.showMessageDialog(null, "Enter Replication Factor.");
//            replicationFactorText.requestFocus();
//            return;
//        }
//        try {
//            replicationFactor = Integer.valueOf(replicationFactorText.getText());
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(null, "number input Replication Factor.");
//            replicationFactorText.requestFocus();
//            return;
//        }
//
//        strategy = (String) strategyBox.getSelectedItem();
//
//        String options = optionText.getText();
//        if (options != null && !options.isEmpty()) {
//            String[] split1 = options.split(",");
//            for (String s : split1) {
//                String[] split2 = s.split("=");
//                if (split2.length != 2) {
//                    JOptionPane.showMessageDialog(null, "Strategy Options format error.");
//                    optionText.requestFocus();
//                    return;
//                }
//                strategyOptions.put(split2[0], split2[1]);
//            }
//        }

        setVisible(false);
        cancel = false;
    }

    /**
     * @return the cancel
     */
    public boolean isCancel() {
        return cancel;
    }

    /**
     * @return the info
     */
    public ColumnFamily getInfo() {
        return info;
    }
}
