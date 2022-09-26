package com.solace.demo.iot;

import com.solace.demo.iot.service.SubscribeRawVideoQueueService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class IOTDemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(IOTDemoApplication.class, args);

		SubscribeRawVideoQueueService subscribeRawVideoQueueService=new SubscribeRawVideoQueueService();
		try{
			subscribeRawVideoQueueService.startRawSubscriber();
		}
		catch(Exception e)
		{
			System.out.println("Inside Main ->"+e.getMessage());
		}
//		SplitFileUsingChannelAPI test = new SplitFileUsingChannelAPI();
//		test.invokesplitFile("/Development/tmp/toystory_1788.mp4",5);
	}
}
