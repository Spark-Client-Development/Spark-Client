package me.wallhacks.spark.manager;

import me.wallhacks.spark.Spark;
import me.wallhacks.spark.event.client.ThreadEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {


    ExecutorService service = Executors.newFixedThreadPool(2);

    public void execute(Runnable command) {
        service.execute(command);
    }


    public ThreadManager() {


        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable ex) {
                System.out.println("Uncaught exception: " + ex);
            }
        };
        Thread t = new Thread() {
            @Override
            public void run(){
                while(true)
                {
                    try {
                        ThreadEvent event = new ThreadEvent();
                        Spark.eventBus.post(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        };
        t.setUncaughtExceptionHandler(h);
        t.setDefaultUncaughtExceptionHandler(h);
        t.start();

    }
}
