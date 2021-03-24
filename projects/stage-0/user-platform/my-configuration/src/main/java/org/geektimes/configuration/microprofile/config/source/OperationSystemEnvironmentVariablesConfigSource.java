package org.geektimes.configuration.microprofile.config.source;

import java.util.Map;

/**
 * 操作系统环境变量 ConfigSource
 */
public class OperationSystemEnvironmentVariablesConfigSource extends MapBasedConfigSource {

    public OperationSystemEnvironmentVariablesConfigSource() {
        super("Operation System Environment Variables", 300);
    }

    @Override
    public void prepareConfigData(Map configData){
        configData.putAll(System.getenv());
    }
}
