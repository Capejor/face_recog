package com.faceRecog.manage.util;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Capejor
 * @className: Test
 * @Description: TODO
 * @date 2019-07-04 11:55
 */
public class Test {



//    public static String reverse(String originStr) {
//        if(originStr == null || originStr.length() <= 1)
//            return originStr;
//        //System.out.println(originStr.substring(1)+originStr.charAt(0));
//
//        return reverse(originStr.substring(1)) + originStr.charAt(0);
//
//    }


            public static void main(String[] args) {
              /*  String str = "ab";
                changeString(str);
                System.out.println("str="+str);*/
            }

            private static void changeString(String str) {
                str = "cd";
            }

       /* Map<String, Object> map = new HashMap();
        map.put("1", 1);
        map.put("2", 2);
        map.put("3", 3);
        map.put("4", 1);
        //视图
        long start1 = System.currentTimeMillis();
        for (String key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }
        long end1 = System.currentTimeMillis();
        System.out.println("第一种---------------" + (end1 - start1));


        long start2 = System.currentTimeMillis();
        for (Map.Entry<String, Object> s : map.entrySet()) {
            System.out.println(s.getKey() + ":" + s.getValue());
        }
        long end2 = System.currentTimeMillis();
        System.out.println("第二种---------------" + (end2 - start2));


        long start3 = System.currentTimeMillis();
        Iterator<String> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            System.out.println(key + ":" + map.get(key));
        }
        long end3 = System.currentTimeMillis();
        System.out.println("第三种---------------" + (end3 - start3));


        long start4 = System.currentTimeMillis();
        Iterator<Map.Entry<String, Object>> iterator1 = map.entrySet().iterator();
        while (iterator1.hasNext()) {
            Map.Entry<String, Object> entry = iterator1.next();
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
        long end4 = System.currentTimeMillis();
        System.out.println("第四种---------------" + (end4 - start4));
*/

       //son sons = new son();
    }


/*
class parent {
    private static int a = 1;
    private static int b;
    private int c = initc();

    static {
        b = 1;
        System.out.println("1.父类静态代码块：赋值b成功");
        System.out.println("1.父类静态代码块：a的值" + a);
    }

    int initc() {
        System.out.println("3.父类成员变量赋值：---> c的值" + c);
        this.c = 12;
        System.out.println("3.父类成员变量赋值：---> c的值" + c);
        return c;
    }

    public parent() {
        System.out.println("4.父类构造方式开始执行---> a:" + a + ",b:" + b);
        System.out.println("4.父类构造方式开始执行---> c:" + c);
    }
}

class son extends parent {
    private static int sa = 1;
    private static int sb;
    private int sc = initc2();

    static {
        sb = 1;
        System.out.println("2.子类静态代码块：赋值sb成功");
        System.out.println("2.子类静态代码块：sa的值" + sa);
    }

    int initc2() {
        System.out.println("5.子类成员变量赋值--->：sc的值" + sc);
        this.sc = 12;
        System.out.println("5.子类成员变量赋值--->：sc的值" + sc);
        return sc;
    }

    public son() {
        System.out.println("6.子类构造方式开始执行---> sa:" + sa + ",sb:" + sb);
        System.out.println("6.子类构造方式开始执行---> sc:" + sc);
    }

    ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
}*/
