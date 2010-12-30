package org.apache.cassandra.gui.gui.control.callback;

public interface PropertiesCallback {
    public void clusterCallback();
    public void keyspaceCallback(String keyspace);
    public void columnFamilyCallback(String keyspace, String columnFamily);
}
