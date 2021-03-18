package org.geektimes.configuration.microprofile.config.source;

import java.util.Map;

/**
 * 动态配置源
 */
public class DynamicConfigSource extends MapBasedConfigSource {

    private Map configData;

    public DynamicConfigSource() {
        super("DynamicConfigSource", 500);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        this.configData = configData;
    }

    public void onUpdate(String data) {
        // 更新（异步）
    }
}
