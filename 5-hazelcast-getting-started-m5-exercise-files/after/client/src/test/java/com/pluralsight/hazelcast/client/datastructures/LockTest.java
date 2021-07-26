package com.pluralsight.hazelcast.client.datastructures;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.pluralsight.hazelcast.client.HazelcastClientTestConfiguration;
import com.pluralsight.hazelcast.client.helper.StorageNodeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Grant Little grant@grantlittle.me
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration( classes = { HazelcastClientTestConfiguration.class })
public class LockTest {

    private ExecutorService executorService;

    @Autowired
    @Qualifier("ClientInstance")
    HazelcastInstance hazelcastInstance;

    @Autowired
    StorageNodeFactory storageNodeFactory;

    @Before
    public void setUp() throws Exception {
        executorService = Executors.newFixedThreadPool(2);
    }

    @After
    public void tearDown() throws Exception {
        executorService.shutdownNow();
    }

    @Test
    public void testLocks() throws Exception {
        PrintOutputRunnable runnable1 = new PrintOutputRunnable(hazelcastInstance, "Runnable One");
        PrintOutputRunnable runnable2 = new PrintOutputRunnable(hazelcastInstance, "Runnable Two");

        Future runnable1Future = executorService.submit(runnable1);
        Thread.sleep(5000);
        Assert.assertTrue(runnable1.ownsLock());
        Future runnable2Future = executorService.submit(runnable2);
        Thread.sleep(5000);
        runnable1Future.cancel(true);
        Thread.sleep(5000);
        Assert.assertTrue(runnable2.ownsLock());
        runnable2Future.cancel(true);
    }






    public class PrintOutputRunnable implements Runnable {

        private HazelcastInstance hazelcastInstance;
        private String name;
        private boolean ownsLock = false;

        public PrintOutputRunnable(HazelcastInstance hazelcastInstance, String name) {
            this.hazelcastInstance = hazelcastInstance;
            this.name = name;
        }

        @Override
        public void run() {
            ILock printingLock = hazelcastInstance.getLock("PrintingLock");
            printingLock.lock();
            try {
                ownsLock = true;
                while(printingLock.isLockedByCurrentThread()) {
                    System.out.println(name + " has lock");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                printingLock.unlock();
                ownsLock = false;
            }
        }

        public boolean ownsLock() {
            return ownsLock;
        }
    }
}
