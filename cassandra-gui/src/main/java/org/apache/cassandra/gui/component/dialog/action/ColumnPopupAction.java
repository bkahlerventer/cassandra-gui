package org.apache.cassandra.gui.component.dialog.action;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.cassandra.gui.component.dialog.CellPropertiesDlg;
import org.apache.cassandra.node.TreeNode;
import org.apache.cassandra.unit.Cell;
import org.apache.cassandra.unit.Key;
import org.apache.cassandra.unit.SColumn;
import org.apache.cassandra.unit.Unit;

public class ColumnPopupAction extends AbstractAction {
    private static final long serialVersionUID = -4419251468566465640L;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    public static final int OPERATION_PROPERTIES = 1;
    public static final int OPERATION_REMOVE = 2;

    private int operation;
    private TreeNode treeNode;

    public ColumnPopupAction(String name,
                             int operation,
                             TreeNode treeNode) {
        this.operation = operation;
        this.treeNode = treeNode;
        putValue(Action.NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        switch (operation) {
        case OPERATION_PROPERTIES:
            if (treeNode.getUnit() == null) {
            } else if (treeNode.getUnit() instanceof Key) {
                insertCell();
            } else if (treeNode.getUnit() instanceof SColumn) {
                insertSuperColumnCell();
            } else if (treeNode.getUnit() instanceof Cell) {
                updateCell();
            }

            break;
        case OPERATION_REMOVE:
            int status = JOptionPane.showConfirmDialog(null,
                                                       "Delete a column " + getName() + "?",
                                                       "confirm",
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.QUESTION_MESSAGE);
            if (status == JOptionPane.YES_OPTION) {
                remove();
            }

            break;
        }
    }

    private void insertCell() {
        Key k = (Key) treeNode.getUnit();

        CellPropertiesDlg cpdlg = new CellPropertiesDlg(k.isSuperColumn() ? CellPropertiesDlg.OPERATION_SUPERCOLUMN_INSERT :
                                                                            CellPropertiesDlg.OPERATION_CELL_INSERT);
        cpdlg.setVisible(true);
        if (cpdlg.isCancel()) {
            return;
        }

        SColumn s = null;
        if (k.isSuperColumn()) {
            s = new SColumn(k, cpdlg.getSuperColumn(), new HashMap<String, Cell>());
        }

        Date d = null;
        try {
            d = treeNode.getClient().insertColumn(treeNode.getKeyspace(),
                                                  treeNode.getColumnFamily(),
                                                  k.getName(),
                                                  s == null ? null : s.getName(),
                                                  cpdlg.getName(),
                                                  cpdlg.getValue());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "error: " + e.getMessage());
            e.printStackTrace();
        }

        if (k.isSuperColumn()) {
            Cell c = new Cell(s, cpdlg.getName(), cpdlg.getValue(), d);
            DefaultMutableTreeNode cn = new DefaultMutableTreeNode(c.getName() + "=" + c.getValue() + ", " +
                                                                   DATE_FORMAT.format(c.getDate()));
            s.getCells().put(c.getName(), c);

            DefaultMutableTreeNode sn = new DefaultMutableTreeNode(s.getName());
            sn.add(cn);
            treeNode.getNode().add(sn);

            treeNode.getUnitMap().put(sn, s);
            treeNode.getUnitMap().put(cn, c);
        } else {
            Cell c = new Cell(k, cpdlg.getName(), cpdlg.getValue(), d);
            DefaultMutableTreeNode cn = new DefaultMutableTreeNode(c.getName() + "=" + c.getValue() + ", " +
                                                                   DATE_FORMAT.format(c.getDate()));
            k.getCells().put(c.getName(), c);
            treeNode.getNode().add(cn);

            treeNode.getUnitMap().put(cn, c);
        }

        treeNode.getTreeModel().reload(treeNode.getNode());
    }

    private void insertSuperColumnCell() {
        SColumn s = (SColumn) treeNode.getUnit();
        CellPropertiesDlg cpdlg = new CellPropertiesDlg(CellPropertiesDlg.OPERATION_CELL_INSERT);
        cpdlg.setVisible(true);
        if (cpdlg.isCancel()) {
            return;
        }

        Key k = (Key) s.getParent();

        Date d = null;
        try {
            d = treeNode.getClient().insertColumn(treeNode.getKeyspace(),
                                                  treeNode.getColumnFamily(),
                                                  k.getName(),
                                                  s.getName(),
                                                  cpdlg.getName(),
                                                  cpdlg.getValue());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "error: " + e.getMessage());
            e.printStackTrace();
        }

        Cell c = new Cell(s, cpdlg.getName(), cpdlg.getValue(), d);
        DefaultMutableTreeNode cn = new DefaultMutableTreeNode(c.getName() + "=" + c.getValue() + ", " +
                                                               DATE_FORMAT.format(c.getDate()));
        s.getCells().put(c.getName(), c);
        treeNode.getUnitMap().put(cn, c);

        treeNode.getNode().add(cn);
        treeNode.getTreeModel().reload(treeNode.getNode());
    }

    private void updateCell() {
        Cell c = (Cell) treeNode.getUnit();
        CellPropertiesDlg cpdlg = new CellPropertiesDlg(CellPropertiesDlg.OPERATION_CELL_UPDATE,
                                                        c.getName(),
                                                        c.getValue());
        cpdlg.setVisible(true);
        if (cpdlg.isCancel()) {
            return;
        }

        Key k = null;
        SColumn s = null;

        Unit parentUnit = c.getParent();
        if (parentUnit instanceof SColumn) {
            s = (SColumn) parentUnit;
            k = (Key) s.getParent();
        } else {
            k = (Key) parentUnit;
        }

        Date d = null;
        try {
            d = treeNode.getClient().insertColumn(treeNode.getKeyspace(),
                                                  treeNode.getColumnFamily(),
                                                  k.getName(),
                                                  s == null ? null : s.getName(),
                                                  cpdlg.getName(),
                                                  cpdlg.getValue());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "error: " + e.getMessage());
            e.printStackTrace();
        }

        c.setName(cpdlg.getName());
        c.setValue(cpdlg.getValue());
        c.setDate(d);

        treeNode.getNode().setUserObject(
                            new DefaultMutableTreeNode(c.getName() + "=" + c.getValue() + ", " +
                                                       DATE_FORMAT.format(c.getDate())));
        treeNode.getTreeModel().nodeChanged(treeNode.getNode());
    }

    private void remove() {
        try {
            if (treeNode.getUnit() instanceof Key) {
                Key k = (Key) treeNode.getUnit();
                treeNode.getClient().removeKey(treeNode.getKeyspace(),
                                               treeNode.getColumnFamily(),
                                               k.getName());

                treeNode.getNode().removeAllChildren();
                treeNode.getTreeModel().reload(treeNode.getNode());
            } else if (treeNode.getUnit() instanceof SColumn) {
                SColumn s = (SColumn) treeNode.getUnit();
                Key k = (Key) s.getParent();
                treeNode.getClient().removeSuperColumn(treeNode.getKeyspace(),
                                                       treeNode.getColumnFamily(),
                                                       k.getName(), 
                                                       s.getName());
                k.getSColumns().remove(s.getName());

                removeNode((DefaultMutableTreeNode) treeNode.getNode().getParent(), treeNode.getNode());
            } else {
                Cell c = (Cell) treeNode.getUnit();
                Unit parent = c.getParent();
                if (parent instanceof Key) {
                    Key k = (Key) parent;
                    treeNode.getClient().removeColumn(treeNode.getKeyspace(),
                                                      treeNode.getColumnFamily(),
                                                      k.getName(),
                                                      c.getName());
                    k.getCells().remove(c.getName());

                    removeNode((DefaultMutableTreeNode) treeNode.getNode().getParent(), treeNode.getNode());
                } else if (parent instanceof SColumn) {
                    SColumn s = (SColumn) parent;
                    Key k = (Key) s.getParent();
                    treeNode.getClient().removeColumn(treeNode.getKeyspace(),
                                                      treeNode.getColumnFamily(),
                                                      k.getName(),
                                                      s.getName(),
                                                      c.getName());
                    s.getCells().remove(c.getName());

                    DefaultMutableTreeNode parentNode =
                        (DefaultMutableTreeNode) treeNode.getNode().getParent();
                    removeNode(parentNode, treeNode.getNode());

                    if (s.getCells().isEmpty()) {
                        k.getSColumns().remove(s.getName());
                        removeNode((DefaultMutableTreeNode) parentNode.getParent(), parentNode);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeNode(DefaultMutableTreeNode parentNode,
                            DefaultMutableTreeNode node) {
        if (parentNode != null && node != null) {
            node.removeFromParent();
            treeNode.getTreeModel().reload(parentNode);
        }
    }

    private String getName() {
        if (treeNode.getUnit() instanceof Key) {
            return ((Key) treeNode.getUnit()).getName();
        } else if (treeNode.getUnit() instanceof SColumn) {
            return ((SColumn) treeNode.getUnit()).getName();
        }

        return ((Cell) treeNode.getUnit()).getName();
    }
}
