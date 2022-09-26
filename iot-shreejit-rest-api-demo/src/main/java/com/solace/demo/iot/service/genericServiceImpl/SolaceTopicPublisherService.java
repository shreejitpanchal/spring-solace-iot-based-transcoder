package com.solace.demo.iot.service.genericServiceImpl;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.publisher.OutboundMessageBuilder;
import com.solace.messaging.publisher.PersistentMessagePublisher;
import com.solace.messaging.resources.Topic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SolaceTopicPublisherService {
    @Value("${com.solace.iot.app.pub.topic.name.prefix:solace/v1/vid/transcode/}")
    private String TOPIC_PREFIX;  // used as the topic "root"

    @Value("${com.solace.iot.app.pub.country.code:sg}")
    private String ctyCode;  // used as the topic "countryCode"

    @Value("${com.solace.iot.transport.host:localhost}")
    private String solaceHost;

    @Value("${com.solace.iot.transport.port:8008}")
    private String solacePort;

    @Value("${com.solace.iot.transport.vpn:default}")
    private String solaceVPN;

    @Value("${com.solace.iot.transport.userName:admin}")
    private String solaceUserName;

    @Value("${com.solace.iot.transport.password:admin}")
    private String solacePassword;

    public void publishVideoToSolaceTopic(String filePath,String fileName, String bitRate, byte[] rawVideoInBytes) {
        try {
            System.out.println("#### Init Solace connection... #### ");
            final Properties properties = getProperties();
            final MessagingService messagingService = MessagingService.builder(ConfigurationProfile.V1)
                    .fromProperties(properties).build().connect();  // blocking connect to the broker

            // create and start the Persistent publisher
            final PersistentMessagePublisher publisher = messagingService.createPersistentMessagePublisherBuilder()
                    .build().start();

            OutboundMessageBuilder messageBuilder = messagingService.messageBuilder();
            try {
                messageBuilder.withProperty("FilePath", filePath);
                messageBuilder.withProperty("FileName", fileName);
                messageBuilder.withProperty("BitRate", bitRate);
                // payload is our "hello world" message from you!
                //OutboundMessage message = messageBuilder.build(String.format("FileName %s!", fileName));
                OutboundMessage message = messageBuilder.build(rawVideoInBytes);
                TOPIC_PREFIX="solace/v1/vid/transcode/";
                ctyCode="sg";
                String topicString = TOPIC_PREFIX + ctyCode + "/" + fileName;
                int bitRateInt = Integer.parseInt(bitRate);

                //Construction of Topic Name as per bit rate
                if (bitRateInt >= 1080) {
                    topicString = topicString + "/144P/240P/360P/480P/720P/1080P/corrID";
                } else if (bitRateInt >= 720) {
                    topicString = topicString + "/144P/240P/360P/480P/720P/corrID";
                } else if (bitRateInt >= 480) {
                    topicString = topicString + "/144P/240P/360P/480P/corrID";
                } else if (bitRateInt >= 360) {
                    topicString = topicString + "/144P/240P/360P/corrID";
                } else {
                    topicString = topicString + "/144P/240P/corrID";
                }

                System.out.printf("#### Publishing to Topic = %s%n", topicString);
                publisher.publish(message, Topic.of(topicString));
//                publisher.terminate(500);
//                messagingService.disconnect();

            } catch (RuntimeException e) {
                //System.out.printf("### Exception caught during producer.send(): %s%n", e);
                publisher.terminate(500);
                messagingService.disconnect();
            }

        } catch (
                Exception ex) {
            System.out.println("Exception main SolaceTopicPubSub : " + ex.getMessage());
        }

    }

    private Properties getProperties() {
        final Properties properties = new Properties();
        properties.setProperty(SolaceProperties.TransportLayerProperties.HOST, "localhost");
        properties.setProperty(SolaceProperties.ServiceProperties.VPN_NAME, "default");     // message-vpn
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_USER_NAME, "admin");      // client-username
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_PASSWORD, "admin");  // client-password
        properties.setProperty(SolaceProperties.ServiceProperties.RECEIVER_DIRECT_SUBSCRIPTION_REAPPLY, "true");  // subscribe Direct subs after reconnect
        return properties;
    }
}
