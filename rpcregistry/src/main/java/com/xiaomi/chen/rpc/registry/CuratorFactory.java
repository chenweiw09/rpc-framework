package com.xiaomi.chen.rpc.registry;

import com.xiaomi.chen.rpc.common.constants.Constants;
import com.xiaomi.chen.rpc.registry.base.Port;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/17
 * @description
 */
@Slf4j
public class CuratorFactory {

    private String registryAddress;

    private static volatile List<CuratorFramework> connectMap = new ArrayList<>();

    public CuratorFactory(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    protected CuratorFramework getConnection() {
        if (connectMap.isEmpty()) {
            log.info("------------创建一次连接");
            connectServer();
        }
        if (connectMap.isEmpty()) {
            throw new RuntimeException("no connection exist");
        }

        return connectMap.get(0);
    }


    private CuratorFramework connectServer() {
        try {

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework client = CuratorFrameworkFactory.newClient(registryAddress,Constants.ZK_SESSION_TIMEOUT, Constants.ZK_SESSION_TIMEOUT,retryPolicy);
            client.start();

            connectMap.add(client);
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            return null;
        }
    }

    protected void createPersistentNode(CuratorFramework client, String path, String data) throws Exception {
        byte[] bytes = StringUtils.isBlank(data) ? "".getBytes():data.getBytes();
        if(client.getZookeeperClient().getZooKeeper().exists(path, false) == null){
            client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, bytes);
        }else{
            log.info("update nodePath:{} with data:{}", path, data);
            client.setData().forPath(path,bytes);
        }

    }


    protected void createEphemeralNode(CuratorFramework client, String path, String data) throws Exception {
        byte[] bytes = StringUtils.isBlank(data) ? "".getBytes():data.getBytes();
        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path,bytes);
    }

    // 递归删除节点的子节点
    protected void delNode(CuratorFramework client, String path) throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public String getNodePath(Port port) {
        return Constants.ZK_REGISTRY_PATH + "/" + port.getInterfaceName() + "/node";
    }

}
