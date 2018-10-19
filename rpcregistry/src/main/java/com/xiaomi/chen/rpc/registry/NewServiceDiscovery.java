package com.xiaomi.chen.rpc.registry;

import com.xiaomi.chen.rpc.common.constants.Constants;
import com.xiaomi.chen.rpc.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/17
 * @description
 */
@Slf4j
public class NewServiceDiscovery extends CuratorFactory {

    private volatile Map<String, List<String>> conMap = new ConcurrentHashMap<>();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private Object lock = new Object();

    public NewServiceDiscovery(String registryAddress) {
        super(registryAddress);

        CuratorFramework client = getConnection();

        if (client != null && client.getState() == CuratorFrameworkState.STARTED) {
            log.info("start to connect to server and watch node");
            try {
                watchNode(client);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("", e);
            }
        }
    }

    private void watchNode(CuratorFramework client) throws Exception{
        setPathCacheListener(client, Constants.ZK_REGISTRY_PATH, true);
    }

    public String discover(String interfaceName) {
        String data = null;
        if (!conMap.isEmpty() && conMap.containsKey(interfaceName)) {
            List<String> dataList = conMap.get(interfaceName);
            data = dataList.size() == 1 ? dataList.get(0) : dataList.get(ThreadLocalRandom.current().nextInt(dataList.size()));
        }
        return data;
    }


    // 监控 指定节点和节点下的所有的节点的变化--无限监听  可以进行本节点的删除(不在创建)
//    private void watchNode(CuratorFramework client) throws Exception {
//
//        TreeCache nodeCache = new TreeCache(client, Constants.ZK_REGISTRY_PATH);
//        nodeCache.start();
//
//        nodeCache.getListenable().addListener((client1, event) -> {
//            ChildData data = event.getData();
//            if (data != null) {
//                switch (event.getType()) {
//                    case NODE_ADDED:
//                        conMap.put(data.getPath(), new ArrayList<>());
//                        log.info("[TreeCache]节点增加, path={}, data={}, conMap:{}", data.getPath(), data.getData(), conMap);
//                        List<String> list = client.getChildren().forPath(data.getPath());
//                        for (String str : list) {
//                            watchChildNode(client1, str);
//                        }
//
//                        break;
//                    case NODE_REMOVED:
//                        log.info("[TreeCache]节点删除, path={}, data={},conMap:{}", data.getPath(), data.getData(), conMap);
//                        conMap.remove(data.getPath());
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//
//    }

    public void setPathCacheListener(CuratorFramework client, String path, boolean cacheData) {
        try {
            PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, cacheData);
            PathChildrenCacheListener childrenCacheListener = (client1, event) -> {
                ChildData data = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("子节点增加, path={}, data={}", data.getPath(), data.getData());
                        break;
                    case CHILD_UPDATED:
                        log.info("子节点更新, path={}, data={}", data.getPath(), data.getData());
                        break;
                    case CHILD_REMOVED:
                        log.info("子节点删除, path={}, data={}", data.getPath(), data.getData());
                        break;
                    default:
                        break;
                }

                rebuild(pathChildrenCache);
            };
            pathChildrenCache.getListenable().addListener(childrenCacheListener);
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            log.error("PathCache监听失败, path=", path);
        }

    }



//    private void buildChildrenCache(CuratorFramework client, String parentPath, boolean cacheData) throws Exception {
//
//        log.info("********watchChildNode:=>parentPath:{}", parentPath);
//
//        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, parentPath, cacheData);
//        pathChildrenCache.start();
//
//        List<ChildData> children = pathChildrenCache.getCurrentData();
//        for(ChildData  data : children){
//            log.info("-------------"+data.getPath());
//        }
//
//        pathChildrenCache.getListenable().addListener((client1, event) -> {
//
//            PathChildrenCacheEvent.Type eventType = event.getType();
//            switch (eventType) {
//                case CONNECTION_RECONNECTED:
//                    log.info("Connection is reconnection.");
//                    break;
//                case CONNECTION_SUSPENDED:
//                    log.info("Connection is suspended.");
//                    break;
//                case CONNECTION_LOST:
//                    log.warn("Connection error,waiting...");
//                    return;
//                case INITIALIZED:
//                    log.warn("Connection init ...");
//                default:
//                    //
//            }
//            pathChildrenCache.rebuild();
//            conMap.clear();
//            rebuild(pathChildrenCache);
//            countDownLatch.countDown();
//        });
//
//
//    }

    protected void rebuild (PathChildrenCache pathChildrenCache) {
        conMap.clear();
        List<ChildData> children = pathChildrenCache.getCurrentData();

        if (children == null || children.isEmpty()) {
            return;
        }

        String path = null;
        for (ChildData data : children) {
            path = data.getPath();
            log.info("path=>{}", path);
            String[] str = path.split("-");
            if(str.length ==3){
                putPort(str[0], str[1]);
            }

        }

    }


    private void putPort(String face, String address) {
        synchronized (lock) {
            if (conMap.containsKey(face)) {
                conMap.get(face).add(address);
                Collections.shuffle(conMap.get(face));
            } else {
                conMap.put(face, new ArrayList<String>() {{
                    add(address);
                }});
            }
        }

    }



}
