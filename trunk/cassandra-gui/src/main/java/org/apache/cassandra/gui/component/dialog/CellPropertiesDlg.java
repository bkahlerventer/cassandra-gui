package org.apache.cassandra.gui.component.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CellPropertiesDlg extends JDialog {
    private static final long serialVersionUID = -7378362468372008181L;

    private class PopupAction extends AbstractAction {
        private static final long serialVersionUID = 4235052996425858520L;

        private static final int ACTION_COPY = 1;

        private JTextField text;
        private int action;

        public PopupAction(String name, JTextField text, int action) {
            this.text = text;
            this.action = action;
            putValue(Action.NAME, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (action) {
            case ACTION_COPY:
                text.copy();
                break;
            }
        }
    }

    private class MousePopup extends MouseAdapter {
        private JTextField text;

        public MousePopup(JTextField text) {
            this.text = text;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                JPopupMenu popup = new JPopupMenu();
                popup.add(new PopupAction("Copy", text, PopupAction.ACTION_COPY));
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    private class EnterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            enterAction();
        }
    }

    private JTextField nameText = new JTextField();
    private JTextField valueText = new JTextField();
    private boolean cancel = true;

    public CellPropertiesDlg(String name, String value){
        nameText.setText(name);
        nameText.setEditable(false);
        valueText.setText(value);
        valueText.addActionListener(new EnterAction());

        JPanel propertiesPane = new JPanel(new GridLayout(2, 2));
        propertiesPane.add(new JLabel("name:"));
        propertiesPane.add(nameText);
        propertiesPane.add(new JLabel("value:"));
        propertiesPane.add(valueText);

        nameText.addMouseListener(new MousePopup(nameText));
        valueText.addMouseListener(new MousePopup(valueText));

        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancel = false;
                setVisible(false);
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
        panel.add(propertiesPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        pack();
        setModalityType(ModalityType.DOCUMENT_MODAL);
        setTitle("Properties");
        setLocationRelativeTo(null);
        setModal(true);
    }

    private void enterAction() {
        if (nameText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter name.");
            nameText.requestFocus();
            return;
        }

        if (valueText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter value.");
            valueText.requestFocus();
            return;
        }

        setVisible(false);
        cancel = false;
    }

    public boolean isCancel() {
        return cancel;
    }

    public String getName() {
        return nameText.getText();
    }

    public String getValue() {
        return valueText.getText();
    }
}
