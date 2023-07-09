package com.common.registry;

import com.utils.zk.CuratorHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry{

    private final CuratorFramework zkClient;

    public ZkServiceRegistry() {
        this.zkClient = CuratorHelper.getZkClient();
        zkClient.start();
    }

    @Override
    public void registerService(String serviceName, InetSocketAddress inetSocketAddress) {
        StringBuilder servicePath = new StringBuilder(CuratorHelper.ZK_REGISTER_ROOT_PATH)
                .append("/")
                .append(serviceName);
        servicePath.append(inetSocketAddress.toString());
        CuratorHelper.createEphemeralNode(zkClient,servicePath.toString());
        log.info("成功创建节点 {}", servicePath);

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
