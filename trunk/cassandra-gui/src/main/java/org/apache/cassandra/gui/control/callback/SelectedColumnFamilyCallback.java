package org.apache.cassandra.gui.control.callback;

public interface SelectedColumnFamilyCallback {
    public void rangeCallback(String keyspaceName,
                              String columnFamilyName,
                              String startKey,
                              String endKey,
                              int rows);
}
