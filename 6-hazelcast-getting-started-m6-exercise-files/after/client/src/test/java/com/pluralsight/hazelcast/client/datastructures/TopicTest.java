package com.pluralsight.hazelcast.client.datastructures;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.pluralsight.hazelcast.client.HazelcastClientTestConfiguration;
import com.pluralsight.hazelcast.client.helper.StorageNodeFactory;
import com.pluralsight.hazelcast.shared.Customer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Grant Little grant@grantlittle.me
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration( classes = { HazelcastClientTestConfiguration.class })
public class TopicTest {

    @Autowired
    StorageNodeFactory storageNodeFactory;

    @Autowired
    @Qualifier("ClientInstance")
    HazelcastInstance hazelcastInstance;

    private ITopic<Customer> newCustomerTopic = null;

    @Test
    public void topicExample() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);

        storageNodeFactory.ensureClusterSize(1);
        newCustomerTopic = hazelcastInstance.getTopic("new-customer-topic");

        String messageListenerRef = newCustomerTopic.addMessageListener(message -> {
            // Do what ever you want to here when you receive the details of the new customer.
            // In my case, just register that it has correctly been received
            latch.countDown();
        });

        try {
            newCustomerTopic.publish(new Customer(1L, "Grant", new Date(), "grant@grantlittle.me"));
            newCustomerTopic.publish(new Customer(2L, "Simon", new Date(), "simon@somecompany.com"));

            Assert.assertTrue(latch.await(1, TimeUnit.SECONDS));

        } finally {
            if (null != newCustomerTopic && null != messageListenerRef) {
                newCustomerTopic.removeMessageListener(messageListenerRef);
            }
        }

    }








    @Bean(name = "ClientInstance")
    public HazelcastInstance clientInstance(StorageNodeFactory storageNodeFactory, ClientConfig config) throws Exception {
        //Ensure there is at least 1 running instance();
        storageNodeFactory.ensureClusterSize(1);
        return HazelcastClient.newHazelcastClient(config);
    }

}
