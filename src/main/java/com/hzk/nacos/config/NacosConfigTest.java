package com.hzk.nacos.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 配置格式：
 * dataId集合：
 * 1、common.yaml,mservice,yaml
 * 2、common.yaml,web,yaml
 *
 * group：mservice，common，web
 *
 * prop，conf等作为dataId
 *
 * 主目标：
 * mq，redis配置，包含IP的配置项
 * log.config单独一个dataId
 */
public class NacosConfigTest {


    public static void main(String[] args) throws Exception{
        String serverAddr = "127.0.0.1:8848";
        String dataId = "nacos-simple-demo.yaml";
        String group = "DEFAULT_GROUP";

        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.USERNAME, "nacos");
        properties.put(PropertyKeyConst.PASSWORD, "nacos");
        ConfigService configService = NacosFactory.createConfigService(properties);
        // 主动
        String config = configService.getConfig(dataId, group, 5000);
        System.out.println(config);


        System.err.println("---------------------------");
        // 监听
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("recieve:" + configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
        System.in.read();
    }

}
