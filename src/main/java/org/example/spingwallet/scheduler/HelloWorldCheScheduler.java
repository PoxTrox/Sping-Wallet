package org.example.spingwallet.scheduler;

import org.springframework.scheduling.annotation.Scheduled;

public class HelloWorldCheScheduler {

    @Scheduled(fixedDelay = 10000)
    public void sayHello() {
        System.out.println("Hello World! ");
    }
}
