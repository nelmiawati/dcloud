/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id.ac.polibatam.mj.dcloud.util;

/**
 *
 * @author mia
 */
public class DcloudConfig extends AConfig {
    
    public enum Param implements IParam {
        
        PACS_ADDRESS("pacs-address", "","^((\\s*[A-Za-z0-9]+@[A-Za-z0-9\\.]+:[0-9]+\\s*,\\s*)*)(\\s*[A-Za-z0-9]+@[A-Za-z0-9\\.]+:[0-9]+\\s*)?$"),
        PACS_THRESHOLD_RECONSTRUCT_NB("pacs-threshold-reconstruct-nb", "0", "[0-9]"),
        DCMRCV_LISTEN_PORT("dcmrcv-listen-port", "11113", "[0-9]"),
        DCMQR_LISTEN_PORT("dcmqr-listen-port", "11114", "[0-9]"),
        ;
        
        private final String name;
        private final String defaultValue;
        private final String pattern;
        
        
        private Param(final String name, final String defaultValue, final String pattern) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.pattern = pattern;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        @Override
        public String getDefaultValue() {
            return this.defaultValue;
        }
        
        @Override
        public String getPattern() {
            return this.pattern;
        }
        
    }
    
    private static final String CONFIG_FILE_NAME = "miadpacs-config.properties";
    private static final DcloudConfig CONFIG = new DcloudConfig();    
    
    private DcloudConfig() {        
        super.loadConfigProperties();        
    }
    
    public static DcloudConfig getInstance() {
        return CONFIG;
    }
    
    @Override
    protected String getConfigFileName() {
        return CONFIG_FILE_NAME;
    }
    
    
}
