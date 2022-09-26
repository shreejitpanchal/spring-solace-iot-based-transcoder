package com.solace.samples.spring.iot.iotshreejittranscoderdemo;

import com.solace.samples.spring.iot.iotshreejittranscoderdemo.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IotShreejitTranscoderDemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(IotShreejitTranscoderDemoApplication.class, args);

        Thread thread144 = new Thread("Raw144") {
            public void run(){
                SubRaw144VideoQueueService subRaw144VideoQueueService=new SubRaw144VideoQueueService();
                subRaw144VideoQueueService.startRawSubscriber();
            }
        };
        thread144.start();

        Thread thread360 = new Thread("Raw360") {
            public void run(){
                SubRaw360VideoQueueService subRaw360VideoQueueService=new SubRaw360VideoQueueService();
                subRaw360VideoQueueService.startRawSubscriber();
            }
        };
        thread360.start();

        Thread thread480 = new Thread("Raw480") {
            public void run(){
                SubRaw480VideoQueueService subRaw480VideoQueueService=new SubRaw480VideoQueueService();
                subRaw480VideoQueueService.startRawSubscriber();
            }
        };
        thread480.start();

        Thread thread720 = new Thread("Raw720") {
            public void run(){
                SubRaw720VideoQueueService subRaw720VideoQueueService=new SubRaw720VideoQueueService();
                subRaw720VideoQueueService.startRawSubscriber();
            }
        };
        thread720.start();

        Thread thread1080 = new Thread("Raw1080") {
            public void run(){
                SubRaw1080VideoQueueService subRaw1080VideoQueueService=new SubRaw1080VideoQueueService();
                subRaw1080VideoQueueService.startRawSubscriber();
            }
        };
        thread1080.start();
    }

}
