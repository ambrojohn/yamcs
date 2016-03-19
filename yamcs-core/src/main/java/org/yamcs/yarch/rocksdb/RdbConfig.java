package org.yamcs.yarch.rocksdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.Options;
import org.yamcs.ConfigurationException;
import org.yamcs.YConfiguration;

/**
 * reads the rdbConfig from the yamcs.yaml and provides RocksDB Options when creating and opening databases 
 * 
 * singleton
 * 
 * @author nm
 *
 */
public class RdbConfig {
    static private RdbConfig instance = new RdbConfig();
    public static final String KEY_rdbConfig = "rdbConfig";
    public static final String KEY_tableConfig = "tableConfig";
    public static final String KEY_cfOptions = "columnFamilyOptions";
    public static final String KEY_tableNamePattern = "tableNamePattern";

    private List<TableConfig> tblConfigList = new ArrayList<TableConfig>();
    
    /**
     * 
     * @return the singleton instance
     */
    public static RdbConfig getInstance() {
        return instance;
    }
    
    @SuppressWarnings("unchecked")
    private RdbConfig() {
        YConfiguration config = YConfiguration.getConfiguration("yamcs");
        if(config.containsKey(KEY_rdbConfig)) {
            Map<String, Object> rdbOptions = config.getMap(KEY_rdbConfig);
            if(rdbOptions.containsKey(KEY_tableConfig)) {
                List<Object> tableConfigs = YConfiguration.getList(rdbOptions, KEY_tableConfig);
                for(Object o: tableConfigs) {
                    if(!(o instanceof Map)) {
                        throw new ConfigurationException("Error in rdbConfig -> tableConfig in yamcs.yaml: the entries of tableConfig have to be maps");
                    }
                    TableConfig tblConf = new TableConfig( (Map<String, Object>)o);
                    tblConfigList.add(tblConf);
                }
            }
        }
        if(getTableConfig("tm")==null) {
            //make more sensible defaults for the tm table
            Map<String, Object> m = new HashMap<String, Object>();
            m.put(KEY_tableNamePattern, "tm");
            Map<String, Object> cfm = new HashMap<String, Object>();
            m.put(KEY_cfOptions, cfm);
            cfm.put("maxWriteBufferNumber", 10*1024);
            TableConfig tm = new TableConfig(m);
            tblConfigList.add(tm);
        }
    }
    
    /**
     *  
     * @param tableName
     * @return the first table config that matches the table name or null if no config matches
     * 
     */
    public TableConfig getTableConfig(String tableName) {
        for(TableConfig tc: tblConfigList) {
            if(tc.tableNamePattern.matcher(tableName).matches()) {
                return tc;
            }
        }
        return null;
    }
    
    public static class TableConfig {
        Pattern tableNamePattern;
        ColumnFamilyOptions cfOptions = new ColumnFamilyOptions();
        //these options are used for the default column family when the database is open
        //for some strange reason we cannot use the cfOptions for that
        Options options = new Options();
        
        long targetFileSizeBase;
        
        TableConfig(Map<String, Object> m) throws ConfigurationException {
            String s = YConfiguration.getString(m, KEY_tableNamePattern);
            try {
                tableNamePattern = Pattern.compile(s);
            } catch (PatternSyntaxException e) {
                throw new ConfigurationException("Cannot parse regexp "+e);
            }
            if(m.containsKey(KEY_cfOptions)) {
                Map<String, Object> cm = YConfiguration.getMap(m, KEY_cfOptions);
                if(cm.containsKey("targetFileSizeBase")) {
                    cfOptions.setTargetFileSizeBase(1024 * YConfiguration.getInt(cm, "targetFileSizeBase"));
                    options.setTargetFileSizeBase(1024 * YConfiguration.getInt(cm, "targetFileSizeBase"));
                }
                if(cm.containsKey("targetFileSizeMultiplier")) {
                    cfOptions.setTargetFileSizeMultiplier(YConfiguration.getInt(cm, "targetFileSizeMultiplier"));
                    options.setTargetFileSizeMultiplier(YConfiguration.getInt(cm, "targetFileSizeMultiplier"));
                }
                if(cm.containsKey("maxBytesForLevelBase")) {
                    cfOptions.setMaxBytesForLevelBase(1024 * YConfiguration.getInt(cm, "maxBytesForLevelBase"));
                    options.setMaxBytesForLevelBase(1024 * YConfiguration.getInt(cm, "maxBytesForLevelBase"));
                }
                if(cm.containsKey("writeBufferSize")) {
                    cfOptions.setWriteBufferSize(1024*YConfiguration.getInt(cm, "writeBufferSize"));
                    options.setWriteBufferSize(1024*YConfiguration.getInt(cm, "writeBufferSize"));
                }
                if(cm.containsKey("maxBytesForLevelMultiplier")) {
                    cfOptions.setMaxBytesForLevelMultiplier(YConfiguration.getInt(cm, "maxBytesForLevelMultiplier"));
                    options.setMaxBytesForLevelMultiplier(YConfiguration.getInt(cm, "maxBytesForLevelMultiplier"));
                }
                if(cm.containsKey("maxWriteBufferNumber")) {
                    cfOptions.setMaxWriteBufferNumber(YConfiguration.getInt(cm, "maxWriteBufferNumber"));
                    options.setMaxWriteBufferNumber(YConfiguration.getInt(cm, "maxWriteBufferNumber"));
                }
            }
        }
        
        public ColumnFamilyOptions getColumnFamilyOptions() {
            return cfOptions;
        }

        public Options getOptions() {
            return options;
        }
    }
}
