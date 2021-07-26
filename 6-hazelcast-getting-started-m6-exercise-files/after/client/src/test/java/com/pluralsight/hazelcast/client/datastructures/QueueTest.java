package com.pluralsight.hazelcast.client.datastructures;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.pluralsight.hazelcast.client.HazelcastClientTestConfiguration;
import com.pluralsight.hazelcast.client.helper.StorageNodeFactory;
import com.pluralsight.hazelcast.shared.Email;
import com.pluralsight.hazelcast.storage.EmailQueueEntry;
import com.pluralsight.hazelcast.storage.StorageNodeApplication;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Grant Little grant@grantlittle.me
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration( classes = { HazelcastClientTestConfiguration.class, StorageNodeApplication.class})
@Configuration
@EntityScan(basePackages = {"com.pluralsight.hazelcast.storage"})
@EnableJpaRepositories
public class QueueTest {

    @Autowired
    StorageNodeFactory storageNodeFactory;

    @Autowired
    @Qualifier("ClientInstance")
    HazelcastInstance hazelcastInstance;

    private IQueue<Email> emailQueue = null;

    @After
    public void tearDown() {
        emailQueue.clear();
    }


    @Test
    public void testQueuePersistence() throws Exception {

        storageNodeFactory.ensureClusterSize(2);

        emailQueue = hazelcastInstance.getQueue("email-queue");

        Email email1 = new Email(UUID.randomUUID().toString(), "address1", "subject1", "body1");
        Email email2 = new Email(UUID.randomUUID().toString(), "address2", "subject2", "body2");
        Email email3 = new Email(UUID.randomUUID().toString(), "address3", "subject3", "body3");

        List<Email> emails = new ArrayList<>(3);
        emails.add(email1);
        emails.add(email2);
        emails.add(email3);

        emailQueue.addAll(emails);

        Email emailFromQueue1 = emailQueue.poll();
        assertEquals(email1, emailFromQueue1);

        storageNodeFactory.ensureClusterSize(0);

        storageNodeFactory.ensureClusterSize(2);

        Email emailFromQueue2 = emailQueue.poll();
        assertEquals(email2, emailFromQueue2);

        Email emailFromQueue3 = emailQueue.poll();
        assertEquals(email3, emailFromQueue3);

        assertEquals(0, emailQueue.size());

    }
}
