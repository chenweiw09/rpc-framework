package com.xiaomi.chen.rpc.registry;

import com.xiaomi.chen.rpc.common.constants.Constants;
import com.xiaomi.chen.rpc.common.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public NewServiceDiscovery(String registryAddress) {
        super(registryAddress);

        CuratorFramework client = getConnection();

        if (client != null && client.getState() == CuratorFrameworkState.STARTED) {
            log.info("start to connect to server and watch node");
            try {
                watchNode(client);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("",e);
            }
        }
    }


    public String discover(String interfaceName) {
        String data = null;
        if(!conMap.isEmpty() && conMap.containsKey(interfaceName)){
            List<String> dataList = conMap.get(interfaceName);
            data = dataList.size() == 1 ? dataList.get(0) : dataList.get(ThreadLocalRandom.current().nextInt(dataList.size()));
        }
        return data;
    }

    // 监控 指定节点和节点下的所有的节点的变化--无限监听  可以进行本节点的删除(不在创建)
    private void watchNode(CuratorFramework client) throws Exception {

        TreeCache nodeCache = new TreeCache(client, Constants.ZK_REGISTRY_PATH);
        nodeCache.start();

        nodeCache.getListenable().addListener((client1, event) -> {
            ChildData data = event.getData();
            if (data != null) {
                switch (event.getType()) {
                    case NODE_ADDED:
                        conMap.put(data.getPath(),new ArrayList<>());
                        log.info("[TreeCache]节点增加, path={}, data={}, conMap:{}", data.getPath(), data.getData(),conMap);
                        List<String> list = client.getChildren().forPath(data.getPath());
                        for(String str : list){
                            watchChildNode(client1, str);
                        }

                        break;
                    case NODE_REMOVED:
                        log.info("[TreeCache]节点删除, path={}, data={},conMap:{}", data.getPath(), data.getData(),conMap);
                        conMap.remove(data.getPath());
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void watchChildNode(CuratorFramework client, String parentPath) throws Exception {
        log.info("********watchChildNode:=>parentPath:{}",parentPath);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, parentPath, true);

        pathChildrenCache.getListenable().addListener((client1, event) -> {
            ChildData data = event.getData();
            if (data != null) {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        log.info("[CHILD_ADDED]节点增加, path={}, data={},------conMap:{}", data.getPath(), data.getData(),conMap);
                        conMap.get(data.getPath()).add(new String(data.getData()));
                        break;
                    case CHILD_REMOVED:
                        log.info("[CHILD_REMOVED]节点删除, path={}, data={},------conMap:{}", data.getPath(), data.getData(),conMap);
                        conMap.get(data.getPath()).remove(new String(data.getData()));
                        break;
                    default:
                        break;
                }
            }
        });

        pathChildrenCache.start();
    }


}
