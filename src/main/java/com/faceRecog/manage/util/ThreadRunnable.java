package com.faceRecog.manage.util;

/**
 * @author Capejor
 * @className: ThreadRunnable
 * @Description: TODO
 * @date 2019-07-18 11:30
 */
public class ThreadRunnable implements Runnable {


    @Override
    public void run() {
        for (int i = 0; i < 21 ; i++) {
            System.out.println(Thread.currentThread()+"--->"+i);
            try {
                Thread.sleep(50);
                //wait(20);
            }catch (InterruptedException e){

            }
        }
    }

    public static void main(String[] args) {
       Thread thread = new Thread(new ThreadRunnable());
       thread.start();
       //thread.notify();
    }
}
