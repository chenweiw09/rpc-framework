package com.xiaomi.chen.rpc.registry;


import com.xiaomi.chen.rpc.common.constants.Constants;
import com.xiaomi.chen.rpc.registry.base.NodePort;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/12
 * @description
 */
@Slf4j
public abstract class RpcRegistryFactory {

    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    private static volatile List<ZooKeeper> connectMap = new ArrayList<>();

    public RpcRegistryFactory(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    protected ZooKeeper getConnection() {
        if (connectMap.isEmpty()) {
            log.info("------------创建一次连接");
            connectServer();
        }
        if (connectMap.isEmpty()) {
            throw new RuntimeException("no connection exist");
        }

        return connectMap.get(0);
    }


    protected void createEphemeralNode(ZooKeeper zk, String data, String znodePath) {
        try {
            byte[] bytes = data.getBytes();

            createParentNode(zk,znodePath);
            //获取父路径
            zk.create(znodePath, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            log.debug("create zookeeper node ({} => {})", znodePath, data);
        } catch (KeeperException | InterruptedException e) {
            log.error("", e);
        }
    }

    private void createParentNode(ZooKeeper zk, String path) throws KeeperException, InterruptedException {

        String[] strs = path.split("\\/");
        if(strs.length <=2){
            return;
        }

        StringBuffer sb = new StringBuffer().append("/");
        for(int i=1;i<strs.length-1;i++){
            String pt = sb.append(strs[i]).toString();
            if(zk.exists(pt, false) == null){
                zk.create(pt,"".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
            sb.append("/");
        }
    }


    protected void createRootNode(ZooKeeper zk, String data, String rootPath) {
        try {
            byte[] bytes = data.getBytes();

            if (zk.exists(rootPath, false) == null) {
                String path = zk.create(rootPath, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.debug("create zookeeper node ({} => {})", path, data);
            }

        } catch (KeeperException | InterruptedException e) {
            log.error("", e);
        }
    }

    public void releaseConnection() {
        Iterator<ZooKeeper> iterator = connectMap.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() != null) {
                try {
                    iterator.next().close();
                    iterator.remove();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private ZooKeeper connectServer() {
        try {
            ZooKeeper zooKeeper = new ZooKeeper(registryAddress, Constants.ZK_SESSION_TIMEOUT, watchedEvent -> {
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    log.info("正在启动连接服务器");
                    if (latch != null) {
                        latch.countDown();
                    }
                } else if (Watcher.Event.KeeperState.Disconnected == watchedEvent.getState()) {
                    log.info("与ZK服务器断开连接");
                } else if (Watcher.Event.KeeperState.Expired == watchedEvent.getState()) {
                    log.info("会话失效");
                }

            });

            latch.await();
            connectMap.add(zooKeeper);
            return zooKeeper;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            log.error("", e);
            return null;
        }
    }

    public String getNodePath(NodePort port) {
        return Constants.ZK_REGISTRY_PATH + "/" + port.getInterfaceName() + "/node";
    }

}
