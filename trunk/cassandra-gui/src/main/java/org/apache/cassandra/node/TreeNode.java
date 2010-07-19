package org.apache.cassandra.node;

import java.io.Serializable;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.cassandra.client.Client;
import org.apache.cassandra.unit.Unit;

public class TreeNode implements Serializable {
    private static final long serialVersionUID = -227448839733721587L;

    private Client client;
    private String keyspace;
    private String columnFamily;
    private DefaultMutableTreeNode node;
    private DefaultTreeModel treeModel;
    private Unit unit;
    private Map<DefaultMutableTreeNode, Unit> unitMap;

    public TreeNode() {
    }

    public TreeNode(Client client,
                    String keyspace,
                    String columnFamily,
                    DefaultMutableTreeNode node,
                    DefaultTreeModel treeModel,
                    Unit unit,
                    Map<DefaultMutableTreeNode, Unit> unitMap) {
        this.client = client;
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;
        this.node = node;
        this.treeModel = treeModel;
        this.unit = unit;
        this.unitMap = unitMap;
    }

    /**
     * @return the client
     */
    public Client getClient() {
        return client;
    }

    /**
     * @param client the client to set
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * @return the keyspace
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * @param keyspace the keyspace to set
     */
    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    /**
     * @return the columnFamily
     */
    public String getColumnFamily() {
        return columnFamily;
    }

    /**
     * @param columnFamily the columnFamily to set
     */
    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    /**
     * @return the node
     */
    public DefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }

    /**
     * @return the treeModel
     */
    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * @param treeModel the treeModel to set
     */
    public void setTreeModel(DefaultTreeModel treeModel) {
        this.treeModel = treeModel;
    }

    /**
     * @return the unit
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * @param unit the unit to set
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * @return the unitMap
     */
    public Map<DefaultMutableTreeNode, Unit> getUnitMap() {
        return unitMap;
    }

    /**
     * @param unitMap the unitMap to set
     */
    public void setUnitMap(Map<DefaultMutableTreeNode, Unit> unitMap) {
        this.unitMap = unitMap;
    }
}
