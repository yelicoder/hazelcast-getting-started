package com.pluralsight.hazelcast.shared.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
@Setter
@Getter
@ToString
public class Customer implements Serializable {
    private static final long serialVersionUID = -8531547371164393019L;
    private Long id;
    private String name;
    private Date dob;
    private String email;

    public Customer(){}

    public Customer(Long id){
        this.id=id;
    }

    public Customer(Long id, String name, Date dob, String email){
        this.id=id;
        this.name=name;
        this.dob=dob;
        this.email=email;
    }


}
