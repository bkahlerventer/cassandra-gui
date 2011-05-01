package org.apache.cassandra.unit;

public class ColumnFamilyMetaData {
    private String columnName;
    private String valiDationClass;
    private String indexType;
    private String indexName;

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the valiDationClass
     */
    public String getValiDationClass() {
        return valiDationClass;
    }

    /**
     * @param valiDationClass the valiDationClass to set
     */
    public void setValiDationClass(String valiDationClass) {
        this.valiDationClass = valiDationClass;
    }

    /**
     * @return the indexType
     */
    public String getIndexType() {
        return indexType;
    }

    /**
     * @param indexType the indexType to set
     */
    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    /**
     * @return the indexName
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * @param indexName the indexName to set
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
