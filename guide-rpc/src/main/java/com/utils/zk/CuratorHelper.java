package com.utils.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.listen.Listenable;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CuratorHelper {

    private static final int SLEEP_MS_BETWEEN_RETRIES = 100;
    private static final int MAX_RETRIES = 3;
    private static final String CONNECT_STRING = "116.204.78.84:2181";
    private static final int CONNECTION_TIMEOUT_MS = 10 * 1000;
    private static final int SESSION_TIMEOUT_MS = 60 * 1000;
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    //serviceName : childrenPath
    private static final ConcurrentHashMap<String , List<String>> serviceMap = new ConcurrentHashMap<>();

    public static CuratorFramework getZkClient(){
        //重连策略：重连3次，中间间隔100ms
        RetryNTimes retryNTimes = new RetryNTimes(MAX_RETRIES, SLEEP_MS_BETWEEN_RETRIES);
        return CuratorFrameworkFactory.builder()
                //所要连接的服务器
                .connectString(CONNECT_STRING)
                //采取的重连策略
                .retryPolicy(retryNTimes)
                //连接超时时间
                .connectionTimeoutMs(CONNECTION_TIMEOUT_MS)
                //会话超时时间
                .sessionTimeoutMs(SESSION_TIMEOUT_MS)
                .build();
    }

    //创建临时节点,session断掉会删除节点
    public static void createEphemeralNode(final CuratorFramework zkCLinet, final String path){
        try {
            zkCLinet.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getChildrenNodes(final CuratorFramework zkClient, final String serviceName){
        try {
            log.info("{}",serviceName);
            //之前注册过直接返回
            if(serviceMap.contains(serviceName)){
                return serviceMap.get(serviceName);
            }
            //第一次注册
            String servicePath = CuratorHelper.ZK_REGISTER_ROOT_PATH + "/" + serviceName;
            List<String> list = zkClient.getChildren().forPath(servicePath);

            serviceMap.put(serviceName,list);
            //第一次注册  需要添加监听器
            registerWatch(zkClient,serviceName);
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //给每个service设置监听器
    public static void registerWatch(CuratorFramework zkClient, String serviceName){
        String servicePath = CuratorHelper.ZK_REGISTER_ROOT_PATH + "/" + serviceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);

        PathChildrenCacheListener pathChildrenCacheListener = ((curatorFramework, pathChildrenCacheEvent) -> {
            List<String> list = curatorFramework.getChildren().forPath(servicePath);
            serviceMap.put(serviceName,list);
        });

        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);

        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            log.error("occur exception {}",e);
            throw new RuntimeException(e);
        }

    }

}
