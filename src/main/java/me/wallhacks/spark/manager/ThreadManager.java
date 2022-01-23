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
        new Thread() {
            public void run(){

                while(true)
                {
                    ThreadEvent event = new ThreadEvent();
                    Spark.eventBus.post(event);
                }
            }

        }.start();
    }
}
