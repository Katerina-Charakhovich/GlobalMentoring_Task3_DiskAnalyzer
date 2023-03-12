package com.epam.disk.analizer.scanner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Animation {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static Animation  instance = null;
    private long starTime;

    public static Animation getInstance() {
        if (instance == null) {
            instance = new Animation();
        }
        return instance;

    }
    public void stop() {
        System.out.println("\r Stop analyzing.");
        executorService.shutdown();
    }

    public void start() {
        starTime = System.currentTimeMillis();
        System.out.println("\r Start analyzing.");
        executorService.scheduleAtFixedRate(this::print, 0, 1000, MILLISECONDS);
    }

    private void print() {
        System.out.println("---------------------------------//------------------------------------");
        long start = System.nanoTime();
        System.out.println("\r  Time execution in milliseconds:"+(System.currentTimeMillis() - starTime));
    }
}
