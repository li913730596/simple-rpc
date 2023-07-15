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


}
