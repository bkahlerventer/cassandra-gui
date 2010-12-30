package org.apache.cassandra.gui.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.cassandra.concurrent.IExecutorMBean;
import org.apache.cassandra.gui.gui.util.ByteBufferHelper;
import org.apache.cassandra.gui.node.NodeInfo;
import org.apache.cassandra.gui.node.RingNode;
import org.apache.cassandra.gui.node.Tpstats;
import org.apache.cassandra.gui.unit.Cell;
import org.apache.cassandra.gui.unit.Key;
import org.apache.cassandra.gui.unit.SColumn;
import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.CfDef;
import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ColumnParent;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.InvalidRequestException;
import org.apache.cassandra.thrift.KeyRange;
import org.apache.cassandra.thrift.KeySlice;
import org.apache.cassandra.thrift.KsDef;
import org.apache.cassandra.thrift.NotFoundException;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;
import org.apache.cassandra.thrift.SuperColumn;
import org.apache.cassandra.thrift.TimedOutException;
import org.apache.cassandra.thrift.TokenRange;
import org.apache.cassandra.thrift.UnavailableException;
import org.apache.cassandra.tools.NodeProbe;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


public class Client {
    public static final String DEFAULT_THRIFT_HOST = "localhost";
    public static final int DEFAULT_THRIFT_PORT = 9160;
    public static final int DEFAULT_JMX_PORT = 8080;

    private TTransport transport;
    private TProtocol protocol;
    private Cassandra.Client client;
//    private NodeProbe probe;

    private boolean connected = false;
    private String host;
    private int thriftPort;
    private int jmxPort;

    private String keyspace;
    private String columnFamily;
    private boolean superColumn;

    public Client() {
        this(DEFAULT_THRIFT_HOST, DEFAULT_THRIFT_PORT, DEFAULT_JMX_PORT);
    }

    public Client(String host) {
        this(host, DEFAULT_THRIFT_PORT, DEFAULT_JMX_PORT);
    }

    public Client(String host, int thriftPort, int jmxPort) {
        this.host = host;
        this.thriftPort = thriftPort;
        this.jmxPort = jmxPort;
    }

    public void connect()
            throws IOException, InterruptedException, InvalidRequestException, TException {
        if (!connected) {
        	
            transport = new TSocket(host, thriftPort);
            TTransport tr = new TFramedTransport(transport);
            protocol = new TBinaryProtocol(tr);
            client = new Cassandra.Client(protocol);
            
            
//            client.set_keyspace(keyspace);
//            probe = new NodeProbe(host, jmxPort);
            tr.open();
//            transport.open();
            connected = true;
        }
    }

    public void disconnect() {
        if (connected) {
            transport.close();
            connected = false;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public String describeClusterName() throws TException {    	
        return client.describe_cluster_name();
    }

    public String descriveVersion() throws TException {
        return client.describe_version();
    }

    public String getTokenMap() throws TException {
    	
//        return client.get_string_property("token map");
        return "";
    }

    public String getConfigFile() throws TException {
    	
    	
//        return client.get_string_property("config file");
        return "";
    }

    public List<TokenRange> describeRing(String keyspace)
            throws TException, InvalidRequestException {
//        this.keyspace = keyspace;
        return client.describe_ring(keyspace);
    }

    public RingNode listRing() {
        RingNode r = new RingNode();
       /* NodeProbe probe = new NodeProbe(endpoint, jmxPort);
        
        
        
        r.setRangeMap(probe.getRangeToEndPointMap(null));

        List<Range> ranges = new ArrayList<Range>(r.getRangeMap().keySet());
        Collections.sort(ranges);
        r.setRanges(ranges);

        r.setLiveNodes(probe.getLiveNodes());
        r.setDeadNodes(probe.getUnreachableNodes());
        r.setLoadMap(probe.getLoadMap());*/

        return r;
    }

    public NodeInfo getNodeInfo(String endpoint) throws IOException, InterruptedException {
        NodeProbe p = new NodeProbe(endpoint, jmxPort);

        NodeInfo ni = new NodeInfo();
        ni.setEndpoint(endpoint);
        ni.setLoad(p.getLoadString());
        ni.setGenerationNumber(p.getCurrentGenerationNumber());
        ni.setUptime(p.getUptime() / 1000);

        MemoryUsage heapUsage = p.getHeapMemoryUsage();
        ni.setMemUsed((double) heapUsage.getUsed() / (1024 * 1024));
        ni.setMemMax((double) heapUsage.getMax() / (1024 * 1024));

        return ni;
    }

    public List<Tpstats> getTpstats(String endpoint) throws IOException, InterruptedException {
        List<Tpstats> l = new ArrayList<Tpstats>();

        NodeProbe p = new NodeProbe(endpoint, jmxPort);
        Iterator<Map.Entry<String, IExecutorMBean>> threads = p.getThreadPoolMBeanProxies();
        for (;threads.hasNext();) {
            Map.Entry<String, IExecutorMBean> thread = threads.next();

            Tpstats tp = new Tpstats();
            tp.setPoolName(thread.getKey());

            IExecutorMBean threadPoolProxy = thread.getValue();
            tp.setActiveCount(threadPoolProxy.getActiveCount());
            tp.setPendingTasks(threadPoolProxy.getPendingTasks());
            tp.setCompletedTasks(threadPoolProxy.getCompletedTasks());
            l.add(tp);
        }

        return l;
    }

    public List<KsDef> getKeyspaces() throws TException, InvalidRequestException {
        return client.describe_keyspaces();
    }

    public CfDef getColumnFamily(String keyspace, String columnFamily)
                throws NotFoundException, TException, InvalidRequestException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;
        
        List<CfDef> cfDefs = client.describe_keyspace(keyspace).getCf_defs();
        
        for (CfDef cfDef : cfDefs) {
			if(columnFamily.equals(cfDef.getName())) {
				return cfDef;
			}
		}
        
        return null;
//        return client.describe_keyspace(keyspace).get(columnFamily);
    }

    public List<CfDef> getColumnFamilys(String keyspace)
            throws NotFoundException, TException, InvalidRequestException {
        this.keyspace = keyspace;

//        Set<String> s = new TreeSet<String>();
        return client.describe_keyspace(keyspace).getCf_defs();

    }

    public int countColumnsRecord(String columnFamily, SlicePredicate predicate, byte[] key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
//        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnParent colParent = new ColumnParent(columnFamily);
   
        
        return client.get_count(ByteBufferHelper.toByteBuffer(key), colParent, predicate, ConsistencyLevel.ONE);
    }

    public int countSuperColumnsRecord(String columnFamily, SlicePredicate predicate, String superColumn, byte[] key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
//        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnParent colParent = new ColumnParent(columnFamily);
        colParent.setSuper_column(superColumn.getBytes());
        
        return client.get_count(ByteBufferHelper.toByteBuffer(key), colParent, predicate, ConsistencyLevel.ONE);

    }

    public Date insertColumn(
                             String columnFamily,
                             byte[] key,
                             String superColumn,
                             String columnName,
                             String value)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
//        this.keyspace = keyspace;
        client.set_keyspace(keyspace);
//        this.columnFamily = columnFamily;
        ColumnParent columnParent = new ColumnParent();
        columnParent.setColumn_family(columnFamily);
        
//        ColumnPath colPath = new ColumnPath(columnFamily);
        if (superColumn != null) {
        	columnParent.setSuper_column(superColumn.getBytes());
        }
//        colPath.setColumn(column.getBytes());
        long timestamp = System.currentTimeMillis() * 1000;
        
        Column column = new Column();
        column.setName(columnName.getBytes());
        column.setValue(value.getBytes());
        client.insert(ByteBufferHelper.toByteBuffer(key), columnParent, column, ConsistencyLevel.ONE);
        

        return new Date(timestamp / 1000);
    }

    public void removeKey(String columnFamily, byte[] key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        
        this.columnFamily = columnFamily;

        ColumnPath colPath = new ColumnPath(columnFamily);
        long timestamp = System.currentTimeMillis() * 1000;
        client.remove(ByteBufferHelper.toByteBuffer(key), colPath, timestamp, ConsistencyLevel.ONE);
        
    }

    public void removeSuperColumn(String columnFamily, byte[] key, String superColumn)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        ColumnPath colPath = new ColumnPath(columnFamily);
        colPath.setSuper_column(superColumn.getBytes());
        long timestamp = System.currentTimeMillis() * 1000;
        client.remove(ByteBufferHelper.toByteBuffer(key), colPath, timestamp, ConsistencyLevel.ONE);
    }

    public void removeColumn(String columnFamily, byte[] key, String column)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnPath colPath = new ColumnPath(columnFamily);
        colPath.setColumn(column.getBytes());
        long timestamp = System.currentTimeMillis() * 1000;
        client.remove(ByteBufferHelper.toByteBuffer(key), colPath, timestamp, ConsistencyLevel.ONE);
    }

    public void removeColumn(String columnFamily, byte[] key, String superColumn, String column)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        ColumnPath colPath = new ColumnPath(columnFamily);
        colPath.setSuper_column(superColumn.getBytes());
        colPath.setColumn(column.getBytes());
        long timestamp = System.currentTimeMillis() * 1000;
        client.remove(ByteBufferHelper.toByteBuffer(key), colPath, timestamp, ConsistencyLevel.ONE);
    }

    public Map<String, Key> getKey(String columnFamily, String superColumn, byte[] key)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException {
        this.keyspace = keyspace;
        this.columnFamily = columnFamily;

        Map<String, Key> m = new TreeMap<String, Key>();

        ColumnParent columnParent = new ColumnParent(columnFamily);
        if (superColumn != null) {
            columnParent.setSuper_column(superColumn.getBytes());
        }

        SliceRange sliceRange = new SliceRange();
        sliceRange.setStart(new byte[0]);
        sliceRange.setFinish(new byte[0]);

        SlicePredicate slicePredicate = new SlicePredicate();
        slicePredicate.setSlice_range(sliceRange);

        List<ColumnOrSuperColumn> l =
            client.get_slice(ByteBufferHelper.toByteBuffer(key), columnParent, slicePredicate, ConsistencyLevel.ONE);

        Key k = new Key(key, new TreeMap<String, SColumn>(), new TreeMap<String, Cell>());
        for (ColumnOrSuperColumn column : l) {
            k.setSuperColumn(column.isSetSuper_column());
            if (column.isSetSuper_column()) {
                SuperColumn scol = column.getSuper_column();
                SColumn s = new SColumn(k, new String(scol.getName(), "UTF8"), new TreeMap<String, Cell>());
                for (Column col : scol.getColumns()) {
                    Cell c = new Cell(s,
                                      new String(col.getName(), "UTF8"),
                                      new String(col.getValue(), "UTF8"),
                                      new Date(col.getTimestamp() / 1000));
                    s.getCells().put(c.getName(), c);
                }

                k.getSColumns().put(s.getName(), s);
            } else {
                Column col = column.getColumn();
                Cell c = new Cell(k,
                                  new String(col.getName(), "UTF8"),
                                  new String(col.getValue(), "UTF8"),
                                  new Date(col.getTimestamp() / 1000));
                k.getCells().put(c.getName(), c);
            }

            m.put(k.getNameStr(), k);
        }

        return m; 
    }

    public Map<String, Key> listKeyAndValues(String keyspace, String columnFamily, String startKey, String endKey, int rows)
            throws InvalidRequestException, UnavailableException, TimedOutException, TException, UnsupportedEncodingException {
        this.keyspace = keyspace;
        client.set_keyspace(keyspace);
        this.columnFamily = columnFamily;

        Map<String, Key> m = new TreeMap<String, Key>();

        ColumnParent columnParent = new ColumnParent(columnFamily);
        

        SliceRange sliceRange = new SliceRange();
        sliceRange.setStart(new byte[0]);
        sliceRange.setFinish(new byte[0]);
        
        SlicePredicate slicePredicate = new SlicePredicate();
        slicePredicate.setSlice_range(sliceRange);

        KeyRange keyRange = new KeyRange(rows);
        
  /*      byte[] startKeyBA = new byte[0];
        if(!startKey.equals("")) {
        	startKeyBA = startKey.getBytes(); 
        }
        
        byte[] endKeyBA = new byte[0];
        if(!endKey.equals("")) {
        	endKeyBA = endKey.getBytes(); 
        }*/
        
        keyRange.setStart_key(new byte[0]);
        keyRange.setEnd_key(new byte[0]);

        List<KeySlice> keySlices =
            client.get_range_slices(columnParent, slicePredicate, keyRange, ConsistencyLevel.ONE);
        for (KeySlice keySlice : keySlices) {
            Key key = new Key(keySlice.getKey(), new TreeMap<String, SColumn>(), new TreeMap<String, Cell>());

//            System.out.println("column size: " + keySlice.getColumns().size());
            for (ColumnOrSuperColumn column : keySlice.getColumns()) {
            	
                key.setSuperColumn(column.isSetSuper_column());
                if (column.isSetSuper_column()) {
                    SuperColumn scol = column.getSuper_column();
                    SColumn s = new SColumn(key, new String(scol.getName(), "UTF8"), new TreeMap<String, Cell>());
                    for (Column col : scol.getColumns()) {
                    	
//                    	System.out.println("col: " + new String(col.getName()) + " value: " + new String(col.getValue()));
                        Cell c = new Cell(s,
                        				  new String(col.getName(), "UTF8"),
                                          new String(col.getValue(), "UTF8"),
                                          new Date(col.getTimestamp() / 1000));
                        s.getCells().put(c.getName(), c);
                    }

                    key.getSColumns().put(s.getName(), s);
                } else {
                    Column col = column.getColumn();
                    Cell c = new Cell(key,
                                      new String(col.getName(), "UTF8"),
                                      new String(col.getValue(), "UTF8"),
                                      new Date(col.getTimestamp() / 1000));
                    key.getCells().put(c.getName(), c);
                }
            }

            m.put(key.getNameStr(), key);
        }

        return m;
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
     * @return the superColumn
     */
    public boolean isSuperColumn() {
        return superColumn;
    }

    /**
     * @param superColumn the superColumn to set
     */
    public void setSuperColumn(boolean superColumn) {
        this.superColumn = superColumn;
    }

	public Cassandra.Client getClient() {
		return client;
	}
}
