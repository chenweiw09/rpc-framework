package com.xiaomi.chen.rpc;

import com.xiaomi.chen.rpc.service.IHello;
import com.xiaomi.chen.rpc.service.IWorld;
import com.xiaomi.chen.rpc.service.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootTestApplicationTests implements ApplicationContextAware{

	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {
	}


	@Test
	public void test2(){
//		RpcProxy rpcProxy = applicationContext.getBean(RpcProxy.class);

//		IHello iHello = rpcProxy.create(IHello.class);

//		IHello iHello = (IHello) applicationContext.getBean(IHello.class.getName());
//
//		if(iHello != null){
//			String str = iHello.sayHi("hello world",23);
//			System.out.println(str);
//		}

		IWorld iWorld = (IWorld) applicationContext.getBean(IWorld.class.getName());

		iWorld.dd();
		iWorld.hsay("ddd");
		iWorld.say("dff",12);

		Person p = new Person("xioaming",23);
		iWorld.todo(p);

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
