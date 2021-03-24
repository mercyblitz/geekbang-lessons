package org.geektimes.configuration.microprofile.config.source;

import java.util.HashMap;
import java.util.Map;

public class JavaSystemPropertiesConfigSource extends MapBasedConfigSource {

    public JavaSystemPropertiesConfigSource() {
        super("Java System Properties", 400);
    }

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * -Dapplication.name=user-web
     *
     * @return {@link System#getProperties()}
     */
    @Override
    public void prepareConfigData(Map configData){
        configData.putAll(System.getProperties());
    }

}
