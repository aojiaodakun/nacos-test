package com.hzk.nacos.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class WebConfigTest {

    private ConfigService configService;
    private static Properties properties = new Properties();
    private static final String serverAddr = "127.0.0.1:8848";


    @Before
    public void before(){
        properties.put("serverAddr", serverAddr);
    }

    @After
    public void after(){
        try {
            configService.shutDown();
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }


    /**
     * group=mservice
     * log.config
     */
    @Test
    public void logConfigTest() throws Exception {
        // 命名空间id
        properties.put("namespace", "sit");
        configService = NacosFactory.createConfigService(properties);

        String dataId = "prop.yaml";
        String group = "web";
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String config = configService.getConfigAndSignListener(dataId, group, 5000, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.err.println("---------------recieve---------------");
                System.out.println(configInfo);
                countDownLatch.countDown();
            }
            @Override
            public Executor getExecutor() {
                return null;
            };
        });
        System.out.println(config);
        Yaml yaml = new Yaml();
        Map<String, Object> configMap = yaml.load(config);

        // 复杂配置项处理
        if (configMap.containsKey("complex.config.dataid")) {
            List<String> complexConfigList = (List<String>)configMap.get("complex.dataids");
            for(String tempDataId : complexConfigList) {
                String value = configService.getConfig(tempDataId, group, 5000);
                System.out.println("complexDataId=" + tempDataId + ",complexValue=" + value);

            }
        }



        countDownLatch.await();
    }

}
