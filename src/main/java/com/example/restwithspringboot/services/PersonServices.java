package com.example.restwithspringboot.services;

import com.example.restwithspringboot.exceptions.ResourceNotFoundException;
import com.example.restwithspringboot.model.Person;
import com.example.restwithspringboot.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonServices {
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(Person.class.getName());

    @Autowired
    PersonRepository repository;

    public Person findById(Long id){
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
    }

    public List<Person> findAll(){

        return repository.findAll();
    }

    public Person create(Person person){

        return repository.save(person);
    }

    public Person update(Person person){

        Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setGender(person.getGender());
        entity.setAddress(person.getAddress());
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());

        return repository.save(person);
    }

    public void delete(Long id){

        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);

    }
}
