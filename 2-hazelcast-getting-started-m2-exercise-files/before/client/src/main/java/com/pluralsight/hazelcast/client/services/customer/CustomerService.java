package com.pluralsight.hazelcast.client.services.customer;


import com.pluralsight.hazelcast.shared.dao.MapNames;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.pluralsight.hazelcast.shared.dao.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;

public class CustomerService implements MapNames {

    private final static String CUSTOMERS_MAP = "customerMap";
    private HazelcastInstance hazelcastInstance;
    private IMap<Long, Customer> customersMap;

    @Autowired
    public CustomerService(@Qualifier("ClientInstance") HazelcastInstance hazelcastInstance)
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    @PostConstruct
    public void init() {
        customersMap = hazelcastInstance.getMap(CUSTOMERS_MAP);
    }
}
