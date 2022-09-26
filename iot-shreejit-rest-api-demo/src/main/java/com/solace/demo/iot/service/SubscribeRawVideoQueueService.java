package com.solace.demo.iot.service;

import com.solace.demo.iot.service.genericServiceImpl.SolaceTopicPublisherService;
import com.solace.demo.iot.service.genericServiceImpl.VideoAnalyserService;
import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.receiver.PersistentMessageReceiver;
import com.solace.messaging.resources.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;
import java.util.UUID;

@Service
public class SubscribeRawVideoQueueService {
//    @Autowired
//    private VideoAnalyserService videoAnalyserService;
//
//    @Autowired
//    private SolaceTopicPublisherService solaceTopicPublisherService;
     private static final String SAMPLE_NAME = SubscribeRawVideoQueueService.class.getSimpleName();
    private static final String QUEUE_NAME = "raw/iot/video/stream";
    private static final String API = "Java";

    private static volatile int msgRecvCounter = 0;                 // num messages received
    private static volatile boolean hasDetectedRedelivery = false;  // detected any messages being redelivered?
    private static volatile boolean isShutdown = false;             // are we done?

    public static String fileUploadDir="/Development/EnterpriseSANStorage/uploads/";

    public void startRawSubscriber()  {

        System.out.println(API + " " + SAMPLE_NAME + " initializing...");
        final Properties properties = getProperties();

        // ready to connect now
        final MessagingService messagingService = MessagingService.builder(ConfigurationProfile.V1)
                .fromProperties(properties)
                .build();
        messagingService.connect();  // blocking connect
        messagingService.addServiceInterruptionListener(serviceEvent -> {
            System.out.println("### SERVICE INTERRUPTION: "+serviceEvent.getCause());
            //isShutdown = true;
        });
        messagingService.addReconnectionAttemptListener(serviceEvent -> {
            System.out.println("### RECONNECTING ATTEMPT: "+serviceEvent);
        });
        messagingService.addReconnectionListener(serviceEvent -> {
            System.out.println("### RECONNECTED: "+serviceEvent);
        });


        final PersistentMessageReceiver receiver = messagingService
                .createPersistentMessageReceiverBuilder()
                .build(Queue.durableExclusiveQueue(QUEUE_NAME));

        try {
            receiver.start();
        } catch (RuntimeException e) {
            System.err.printf("%n*** Could not establish a connection to queue '%s': %s%n", QUEUE_NAME, e.getMessage());
            System.err.println("Exiting.");
            return;
        }

        // asynchronous anonymous receiver message callback
        receiver.receiveAsync(message -> {
            msgRecvCounter++;
            System.out.println("--------Message start --------");
            System.out.println(String.valueOf(message.getPayloadAsString().getBytes()));
            System.out.println("Customer Header:: FileName ::"+message.getProperty("JMS_Solace_HTTP_field_filename"));

            String localFileName=message.getProperty("JMS_Solace_HTTP_field_filename");
            String localFileExtension=message.getProperty("JMS_Solace_HTTP_field_fileExtension");

            if(localFileName.isEmpty()){
                localFileName = "Raw-"+UUID.randomUUID().toString().substring(0, 4);
            }
            File file = new File(fileUploadDir+"/"+localFileName+"."+localFileExtension);

            System.out.println("Final file Path::"+file.getAbsolutePath());
            System.out.println("--------Message end --------");

            try (FileOutputStream fos = new FileOutputStream(file);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 DataOutputStream dos = new DataOutputStream(bos))
            {
                dos.write(message.getPayloadAsBytes()); // Write Bytes to File Stream
                System.out.println("Successfully written data to the file");
                System.out.println("----- Invoking Orchestration Start ------");
                VideoAnalyserService videoAnalyserService=new VideoAnalyserService();
                String bitRate = videoAnalyserService.getBitRate(file.getAbsolutePath()); // Analysis

                SolaceTopicPublisherService solaceTopicPublisherService=new SolaceTopicPublisherService();
                solaceTopicPublisherService.publishVideoToSolaceTopic(file.getAbsolutePath(),file.getName(), bitRate,message.getPayloadAsBytes());
                System.out.println("----- Invoking Orchestration End ------");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (message.isRedelivered()) {  // useful check
                hasDetectedRedelivery = true;
            }
            receiver.ack(message);  // ACKs are asynchronous
        });

        // async queue receive working now, so time to wait until done...
        System.out.println(SAMPLE_NAME + " connected, and running. Press [ENTER] to quit.");
        try {
            while (System.in.available() == 0 && !isShutdown) {
                Thread.sleep(4000);  // wait 1 second
                System.out.printf("%s %s Received msgs/s: %,d%n", API, SAMPLE_NAME, msgRecvCounter);  // simple way of calculating message rates
                msgRecvCounter = 0;
                if (hasDetectedRedelivery) {
                    System.out.println("*** Redelivery detected ***");
                    hasDetectedRedelivery = false;  // only show the error once per second
                }
            }
            isShutdown = true;
            receiver.terminate(1500L);
            Thread.sleep(4000);
            messagingService.disconnect();
        }
        catch(Exception e){
            System.out.println("Exception in SubscribeRawVideo-"+e.getMessage());
        }
        System.out.println("Main thread quitting.");
    }

    private static Properties getProperties() {
        final Properties properties = new Properties();
        properties.setProperty(SolaceProperties.TransportLayerProperties.HOST, "localhost");          // host:port
        properties.setProperty(SolaceProperties.ServiceProperties.VPN_NAME,  "default");     // message-vpn
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_USER_NAME, "admin");      // client-username
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_PASSWORD, "admin");  // client-password
        //properties.setProperty(JCSMPProperties.GENERATE_SEQUENCE_NUMBERS, true);  // not required, but interesting
        properties.setProperty(SolaceProperties.TransportLayerProperties.RECONNECTION_ATTEMPTS, "20");  // recommended settings
        properties.setProperty(SolaceProperties.TransportLayerProperties.CONNECTION_RETRIES_PER_HOST, "5");
        return properties;
    }

}
