package com.xiaomi.chen.rpc.service;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/18
 * @description
 */
@Data
public class Person implements Serializable {
    String name;

    int age;


    public Person() {
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{----" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
