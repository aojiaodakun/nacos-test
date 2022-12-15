package com.hzk.nacos.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class ConfigTest {

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
     * 简单获取namespace=public的配置项
     */
    @Test
    public void getConfigTest() throws Exception {
        configService = NacosFactory.createConfigService(properties);

        String dataId = "nacos-simple-demo.yaml";
        String group = "DEFAULT_GROUP";
        String config = configService.getConfig(dataId, group, 5000);
        System.out.println(config);
    }


    /**
     * 简单获取namespace=public的配置项,并监听
     */
    @Test
    public void getConfigAndWatchTest() throws Exception {
        configService = NacosFactory.createConfigService(properties);

        String dataId = "nacos-simple-demo.yaml";
        String group = "DEFAULT_GROUP";
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
        countDownLatch.await();
    }


    /**
     * 简单获取namespace=sit的配置项
     */
    @Test
    public void namespaceTest() throws Exception {
        // 命名空间id
        properties.put("namespace", "sit");
        configService = NacosFactory.createConfigService(properties);

        String dataId = "common";
        String group = "DEFAULT_GROUP";
        String config = configService.getConfig(dataId, group, 5000);
        System.out.println(config);
    }

    /**
     * group=common
     * dataId=prop.yaml
     */
    @Test
    public void commonPropYamlTest() throws Exception {
        // 命名空间id
        properties.put("namespace", "sit");
        configService = NacosFactory.createConfigService(properties);

        String dataId = "prop.yaml";
        String group = "common";
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
        Map<String, String> mqServerMap = (Map<String, String>)configMap.get("mq.server");

        countDownLatch.await();
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

        String dataId = "log.config";
        String group = "mservice";
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
        countDownLatch.await();
    }

}
