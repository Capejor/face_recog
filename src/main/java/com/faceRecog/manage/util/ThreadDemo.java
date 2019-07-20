package com.faceRecog.manage.util;

import org.apache.ibatis.reflection.ExceptionUtil;

import java.util.concurrent.Executors;

/**
 * @author Capejor
 * @className: ThreadDemo
 * @Description: TODO
 * @date 2019-07-17 16:19
 */
public class ThreadDemo extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(Thread.currentThread()+"--->"+i);
        }
    }

    public static void main(String[] args) {
        ThreadDemo threadDemo = new ThreadDemo();
        threadDemo.start();
    }
}