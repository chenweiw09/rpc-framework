package com.xiaomi.chen.rpc.client.old;

import com.xiaomi.chen.rpc.client.old.proxy.CglibProxy;
import com.xiaomi.chen.rpc.client.old.proxy.ProxyFactory;
import com.xiaomi.chen.rpc.common.annotation.RpcInterface;
import com.xiaomi.chen.rpc.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Set;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */

@Slf4j
public class RpcClientConfig implements ApplicationContextAware, InitializingBean{

    private ApplicationContext applicationContext;

    private String registryAddress;

    private ServiceDiscovery serviceDiscovery;

    private String basePackages;

    public RpcClientConfig(String registryAddress,String basePackages) {
        this.registryAddress = registryAddress;
        this.basePackages = basePackages;
    }



    @Override
    public void afterPropertiesSet(){
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages(basePackages).addScanners(new SubTypesScanner()).addScanners(new FieldAnnotationsScanner()));

        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        ProxyFactory proxyFactory = new ProxyFactory(getTransporters());

        CglibProxy cglibProxy = new CglibProxy(getTransporters());

        Set<Class<?>> typeClass = reflections.getTypesAnnotatedWith(RpcInterface.class, true);

        for(Class<?> clz : typeClass){
            beanFactory.registerSingleton(clz.getName(), proxyFactory.create(clz));
        }


//        Set<Field> typeClass = reflections.getFieldsAnnotatedWith(RpcInterface.class);
//        for(Field field : typeClass){
//            String beanRegisterName = field.getAnnotation(RpcInterface.class).name();
//            if(beanRegisterName == null || beanRegisterName.length()==0){
//                beanRegisterName = getRegisterBeanName(field);
//            }
//            beanFactory.registerSingleton(beanRegisterName, proxyFactory.create(field.getType()));
//        }

        log.info("afterPropertiesSet is {}",typeClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ProxyFactory getProxyFactory(){
        ProxyFactory proxyFactory = new ProxyFactory(getTransporters());
        return proxyFactory;
    }


    private Transporters getTransporters(){
        if(this.serviceDiscovery == null){
            this.serviceDiscovery = new ServiceDiscovery(registryAddress);
        }
        return new Transporters(serviceDiscovery);
    }


}
