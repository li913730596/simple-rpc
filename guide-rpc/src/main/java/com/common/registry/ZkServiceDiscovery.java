package com.common.registry;

import com.utils.zk.CuratorHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery{
    private final CuratorFramework zkClient;

    public ZkServiceDiscovery() {
        this.zkClient = CuratorHelper.getZkClient();
        zkClient.start();
    }
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        StringBuilder servicePath = new StringBuilder(CuratorHelper.ZK_REGISTER_ROOT_PATH)
                .append("/")
                .append(serviceName);
        log.info("{}",servicePath);

        try {
            String serviceAddress = CuratorHelper.getChildrenNodes(zkClient, serviceName).get(0);

            log.info("成功获取服务 {}  对应的InetAddress {}", serviceName, serviceAddress);

            return new InetSocketAddress(serviceAddress.split(":")[0],
                    Integer.parseInt(serviceAddress.split(":")[1]));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
