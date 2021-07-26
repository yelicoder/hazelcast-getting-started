package com.pluralsight.hazelcast.client.datastructures;

/**
 * Created by Grant Little (grant@grantlittle.me)
 */

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.pluralsight.hazelcast.client.HazelcastClientTestConfiguration;
import com.pluralsight.hazelcast.client.helper.StorageNodeFactory;
import com.pluralsight.hazelcast.shared.Customer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Grant Little grant@grantlittle.me
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration( classes = { HazelcastClientTestConfiguration.class })
public class MultiMapTest {

    @Autowired
    StorageNodeFactory storageNodeFactory;

    @Autowired
    @Qualifier("ClientInstance")
    HazelcastInstance hazelcastInstance;

    @Test
    public void multiMapExample() throws Exception {

        Customer customer1 = new Customer(1L, "Grant Little", null, "grant@grantlittle.me");
        Customer customer2 = new Customer(2L, "Simon", null, "simon@somecompany.com");
        Customer customer3 = new Customer(3L, "Jane", null, "jane@somecompany.com");

        MultiMap<String, Customer> accountToCustomersMap = hazelcastInstance.getMultiMap("account-to-customers");
        accountToCustomersMap.put("1", customer1);
        accountToCustomersMap.put("2", customer2);
        accountToCustomersMap.put("2", customer3);

        Assert.assertEquals(1, accountToCustomersMap.get("1").size());
        Assert.assertEquals(2, accountToCustomersMap.get("2").size());

    }
}