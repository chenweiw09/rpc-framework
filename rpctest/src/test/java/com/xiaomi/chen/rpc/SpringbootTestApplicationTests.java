package com.xiaomi.chen.rpc;

import com.xiaomi.chen.rpc.service.IWorld;
import com.xiaomi.chen.rpc.service.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootTestApplicationTests implements ApplicationContextAware{

	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {
	}


	@Test
	public void test2(){


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
