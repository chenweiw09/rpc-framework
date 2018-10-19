package com.xiaomi.chen.rpc.registry;

import com.xiaomi.chen.rpc.common.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
@Slf4j
public class ServiceDiscovery extends RpcRegistryFactory {
    private Map<String, List<String>> conMap = new ConcurrentHashMap<>();

    private Object lock = new Object();

    public ServiceDiscovery(String registryAddress) {
        super(registryAddress);
        ZooKeeper zk = getConnection();
        if(zk != null){
            log.info("开始初始化服务发现");
            watchNode(zk);
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


    private void watchNode(final ZooKeeper zk){
        try {
            List<String> intsList = zk.getChildren(Constants.ZK_REGISTRY_PATH, watchedEvent -> {
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    watchNode(zk);
                }
            });

            // 首先清空对应的map
            conMap.clear();
            for(String interfaceName : intsList){
                log.info("path=>{}", interfaceName);
                String[] str = interfaceName.split("-");
                if(str.length ==3){
                    putPort(str[0], str[1]);
                }

            }

        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
            log.error("",e);
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
