package com.faceRecog.manage.util;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.jar.JarFile;
 
public class ServerStart extends JarLauncher {
    private static ClassLoader classLoader = null;
    private static ServerStart bootstrap = null;
 
    protected void launch(String[] args, String mainClass, ClassLoader classLoader, boolean wait)
            throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        Thread runnerThread = new Thread(() -> {
            try {
                createMainMethodRunner(mainClass, args, classLoader).run();
            }
            catch(Exception ex) {}
        });
        runnerThread.setContextClassLoader(classLoader);
        runnerThread.setName(Thread.currentThread().getName());
        runnerThread.start();
        if (wait == true) {
            runnerThread.join();
        }
    }
 
    public static void start (String []args) {
        bootstrap = new ServerStart ();
        try {
            JarFile.registerUrlProtocolHandler();
            classLoader = bootstrap.createClassLoader(bootstrap.getClassPathArchives());
            bootstrap.launch(args, bootstrap.getMainClass(), classLoader, true);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
 
    public static void stop (String []args) {
        try {
            if (bootstrap != null) {
                bootstrap.launch(args, bootstrap.getMainClass(), classLoader, true);
                bootstrap = null;
                classLoader = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }
 
    public static void main(String[] args) {
        String mode = args != null && args.length > 0 ? args[0] : null;
        if ("start".equals(mode)) {
            ServerStart.start(args);
        }
        else if ("stop".equals(mode)) {
            ServerStart.stop(args);
        }
    }
}