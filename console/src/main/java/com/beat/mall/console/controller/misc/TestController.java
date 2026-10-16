package com.beat.mall.console.controller.misc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

@Slf4j
@RestController
public class TestController {

    @RequestMapping("/thread/unsafe")
    public String unsafe() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add("1");
        }
        StringBuilder stringBuilder = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(list.size());
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            new Thread(() -> {
                stringBuilder.append(s);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return String.valueOf(stringBuilder.length());
    }

    @RequestMapping("/thread/safe")
    public String safe() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add("1");
        }
        StringBuffer stringBuffer = new StringBuffer();
        CountDownLatch latch = new CountDownLatch(list.size());
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            new Thread(() -> {
                stringBuffer.append(s);
                latch.countDown();
            }).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return String.valueOf(stringBuffer.length());
    }

    @RequestMapping("/thread/threadsafe")
    public int threadSafe() {
        int clientTotal = 5000;
        int threadTotal = 200;
        StringBuffer stringBuffer = new StringBuffer();
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    stringBuffer.append("1");
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executorService.shutdown();
        int length = stringBuffer.length();
        log.info("size:{}", length);
        return length;
    }

    @RequestMapping("/thread/notthreadtafe")
    public int notThreadSafe() {
        int clientTotal = 5000;
        int threadTotal = 200;
        StringBuilder stringBuilder = new StringBuilder();
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);
        for (int i = 0; i < clientTotal; i++) {
            executorService.execute(() -> {
                try {
                    semaphore.acquire();
                    stringBuilder.append("1");
                    semaphore.release();
                } catch (Exception e) {
                    log.error("exception", e);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executorService.shutdown();
        int length = stringBuilder.length();
        log.info("size:{}", length);
        return length;
    }
}
