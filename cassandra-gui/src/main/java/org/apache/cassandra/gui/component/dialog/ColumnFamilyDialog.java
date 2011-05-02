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
import org.apache.cassandra.client.Client.ComparatorType;
import org.apache.cassandra.unit.ColumnFamily;

public class ColumnFamilyDialog extends JDialog {
    private static final long serialVersionUID = -13548946072075769L;

    private class EnterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            enterAction();
        }
    }

    private class ComparatorAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (((String) columnTypeBox.getSelectedItem()).equals(Client.ColumnType.SUPER.toString())) {
                subComparatorTypeBox.setEnabled(true);
            } else {
                subComparatorTypeBox.setEnabled(false);
            }
        }
    }

    private JTextField columnFamilyText = new JTextField();
    private JComboBox columnTypeBox = new JComboBox();
    private JComboBox comparatorTypeBox = new JComboBox();
    private JComboBox subComparatorTypeBox = new JComboBox();
    private JTextField commentText = new JTextField();
    private JTextField rowsCachedText = new JTextField();
    private JTextField rowCacheSavePeriodText = new JTextField();
    private JTextField keysCachedText = new JTextField();
    private JTextField keyCacheSavePeriodText = new JTextField();
    private JTextField readRepairChanceText = new JTextField();
    private JTextField gcGraceText = new JTextField();
    private JTextField memtableOperationsText = new JTextField();
    private JTextField memtableThroughputText = new JTextField();
    private JTextField memtableFlushAfterText = new JTextField();
    private JTextField defaultValidationClassText = new JTextField();
    private JTextField minCompactionThresholdText = new JTextField();
    private JTextField maxCompactionThresholdText = new JTextField();
    private ColumnFamilyMetaDataDialog metaDataDialog;

    private boolean cancel = true;
    private ColumnFamily columnFamily = new ColumnFamily();

    public ColumnFamilyDialog(ColumnFamily columnFamily) {
        this.columnFamily = columnFamily;
        create(columnFamily);
    }

    public ColumnFamilyDialog() {
        create(null);
    }

    public void create(ColumnFamily selectedColumnFamily) {
        columnFamilyText.addActionListener(new EnterAction());
        commentText.addActionListener(new EnterAction());
        rowsCachedText.addActionListener(new EnterAction());
        rowCacheSavePeriodText.addActionListener(new EnterAction());
        keysCachedText.addActionListener(new EnterAction());
        keyCacheSavePeriodText.addActionListener(new EnterAction());
        readRepairChanceText.addActionListener(new EnterAction());
        gcGraceText.addActionListener(new EnterAction());
        memtableOperationsText.addActionListener(new EnterAction());
        memtableThroughputText.addActionListener(new EnterAction());
        memtableFlushAfterText.addActionListener(new EnterAction());
        defaultValidationClassText.addActionListener(new EnterAction());
        minCompactionThresholdText.addActionListener(new EnterAction());
        maxCompactionThresholdText.addActionListener(new EnterAction());

        JPanel inputPanel = new JPanel(new GridLayout(18, 2));

        // ColumnFamily Name
        addJTextField(inputPanel, "Column Family Name: ", columnFamilyText);

        // Column Type
        for (ColumnType ct : Client.ColumnType.values()) {
            columnTypeBox.addItem(ct.toString());
        }
        if (selectedColumnFamily != null) {
            columnTypeBox.setSelectedItem(selectedColumnFamily.getColumnType());
        } else {
            columnTypeBox.setSelectedItem("");
        }
        columnTypeBox.addActionListener(new ComparatorAction());
        inputPanel.add(new JLabel("Column Type: "));
        inputPanel.add(columnTypeBox);

        // Comparator
        for (ComparatorType ct : Client.ComparatorType.values()) {
            comparatorTypeBox.addItem(ct.toString());
        }
        if (selectedColumnFamily != null) {
            comparatorTypeBox.setSelectedItem(selectedColumnFamily.getComparator());
        }
        inputPanel.add(new JLabel("Comparator Type: "));
        inputPanel.add(comparatorTypeBox);

        // SubComparator
        for (ComparatorType ct : Client.ComparatorType.values()) {
            subComparatorTypeBox.addItem(ct.toString());
        }
        if (selectedColumnFamily != null) {
            subComparatorTypeBox.setSelectedItem(selectedColumnFamily.getSubcomparator());
        } else {
            subComparatorTypeBox.setSelectedItem("");
        }
        inputPanel.add(new JLabel("SubComparator Type: "));
        inputPanel.add(subComparatorTypeBox);

        // comment
        addJTextField(inputPanel, "comment: ", commentText);

        // Rows Cached
        addJTextField(inputPanel, "rows cached: ", rowsCachedText);

        // Row Cached Save Period
        addJTextField(inputPanel, "rows cached save period: ", rowCacheSavePeriodText);

        // Keys Cached
        addJTextField(inputPanel, "keys cached: ", keysCachedText);

        // Key Cached Save Period
        addJTextField(inputPanel, "Key Cached Save Period: ", keyCacheSavePeriodText);

        // Read Repair Chance
        addJTextField(inputPanel, "Read Repair Chance: ", readRepairChanceText);

        // GC Grace
        addJTextField(inputPanel, "GC Grace: ", gcGraceText);

        // Memtable Operations
        addJTextField(inputPanel, "Memtable Operations: ", memtableOperationsText);

        // MemTable Throughput
        addJTextField(inputPanel, "Table Throughput: ", memtableThroughputText);

        // MemTable Flush After
        addJTextField(inputPanel, "MemTable Flush After: ", memtableFlushAfterText);

        // Default Validation Class
        addJTextField(inputPanel, "Default Validation Class: ", defaultValidationClassText);

        // Min Compaction Threshold
        addJTextField(inputPanel, "Min Compaction Threshold: ", minCompactionThresholdText);

        // Max Compaction Threshold
        addJTextField(inputPanel, "Max Compaction Threshold: ", maxCompactionThresholdText);

        // column metadata
        metaDataDialog = new ColumnFamilyMetaDataDialog(columnFamily);
        inputPanel.add(new JLabel("Column MetaData: "));
        JButton detail = new JButton("detail");
        detail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                metaDataDialog.setVisible(true);
            }
        });
        inputPanel.add(detail);

        // buttons
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

    private void addJTextField(JPanel inputPanel, String label, JTextField field) {
        inputPanel.add(new JLabel(label));
        inputPanel.add(field);
    }

    private void enterAction() {
        // ColumnFamily Name
        if (columnFamilyText.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Enter Column Family Name.");
            columnFamilyText.requestFocus();
            return;
        }
        columnFamily.setColumnFamilyName(columnFamilyText.getText());

        // Column Type
        columnFamily.setColumnType((String) columnTypeBox.getSelectedItem());

        // Comparator
        columnFamily.setComparator((String) comparatorTypeBox.getSelectedItem());

        // SubComparator Type
        if (((String) columnTypeBox.getSelectedItem()).equals(Client.ColumnType.SUPER.toString())) {
            columnFamily.setSubcomparator((String) subComparatorTypeBox.getSelectedItem());
        }

        // comment
        columnFamily.setComment(commentText.getText());

        // Rows Cached
        if (!rowsCachedText.getText().isEmpty()) {
            try {
                Double.valueOf(rowsCachedText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Rows Cached.");
                rowsCachedText.requestFocus();
                return;
            }
            columnFamily.setRowsCached(rowsCachedText.getText());
        }

        // Row Cached Save Period
        if (!rowCacheSavePeriodText.getText().isEmpty()) {
            try {
                Integer.valueOf(rowCacheSavePeriodText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Key Cached Save Period.");
                rowCacheSavePeriodText.requestFocus();
                return;
            }
            columnFamily.setRowsCached(rowCacheSavePeriodText.getText());
        }

        // Keys Cached
        if (!keysCachedText.getText().isEmpty()) {
            try {
                Double.valueOf(keysCachedText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Keys Cached.");
                keysCachedText.requestFocus();
                return;
            }
            columnFamily.setKeysCached(keysCachedText.getText());
        }

        // Key Cached Save Period
        if (!keyCacheSavePeriodText.getText().isEmpty()) {
            try {
                Integer.valueOf(keyCacheSavePeriodText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Key Cached Save Period.");
                keyCacheSavePeriodText.requestFocus();
                return;
            }
            columnFamily.setKeyCacheSavePeriod(keyCacheSavePeriodText.getText());
        }

        // Read Repair Chance
        if (!readRepairChanceText.getText().isEmpty()) {
            try {
                Double.valueOf(readRepairChanceText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Read Repair Chance.");
                readRepairChanceText.requestFocus();
                return;
            }
            columnFamily.setReadRepairChance(readRepairChanceText.getText());
        }

        // GC Grace
        if (!gcGraceText.getText().isEmpty()) {
            try {
                Integer.valueOf(gcGraceText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input GC Grace.");
                gcGraceText.requestFocus();
                return;
            }
            columnFamily.setGcGrace(gcGraceText.getText());
        }

        // Memtable Operations
        if (!memtableOperationsText.getText().isEmpty()) {
            try {
                Double.valueOf(memtableOperationsText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Memtable Operations.");
                memtableOperationsText.requestFocus();
                return;
            }
            columnFamily.setMemtableOperations(memtableOperationsText.getText());
        }

        // MemTable Throughput
        if (!memtableThroughputText.getText().isEmpty()) {
            try {
                Integer.valueOf(memtableThroughputText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input MemTable Throughput.");
                memtableThroughputText.requestFocus();
                return;
            }
            columnFamily.setMemtableThroughput(memtableThroughputText.getText());
        }

        // MemTable Flush After
        if (!memtableFlushAfterText.getText().isEmpty()) {
            try {
                Integer.valueOf(memtableFlushAfterText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input MemTable Flush After.");
                memtableFlushAfterText.requestFocus();
                return;
            }
            columnFamily.setMemtableFlushAfter(memtableFlushAfterText.getText());
        }

        // Default Validation Class
        columnFamily.setDefaultValidationClass(defaultValidationClassText.getText());

        // Min Compaction Threshold
        if (!minCompactionThresholdText.getText().isEmpty()) {
            try {
                Integer.valueOf(minCompactionThresholdText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Min Compaction Threshold.");
                minCompactionThresholdText.requestFocus();
                return;
            }
            columnFamily.setMinCompactionThreshold(minCompactionThresholdText.getText());
        }

        // Max Compaction Threshold
        if (!maxCompactionThresholdText.getText().isEmpty()) {
            try {
                Integer.valueOf(maxCompactionThresholdText.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "number input Max Compaction Threshold.");
                maxCompactionThresholdText.requestFocus();
                return;
            }
            columnFamily.setMaxCompactionThreshold(maxCompactionThresholdText.getText());
        }

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
    public ColumnFamily getColumnFamily() {
        return columnFamily;
    }
}
