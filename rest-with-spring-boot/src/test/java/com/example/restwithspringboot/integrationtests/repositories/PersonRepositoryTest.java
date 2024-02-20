package com.example.restwithspringboot.integrationtests.repositories;

import com.example.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.restwithspringboot.model.Person;
import com.example.restwithspringboot.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {
    @Autowired
    PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void setup(){
        person = new Person();
    }

    @Test
    @Order(1)
    public void testFindByName(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC,"firstName"));
        person = repository.findPersonByFirstName("ayr", pageable).getContent().getFirst();

        assertFalse(person.getEnabled());

        assertEquals(821 ,person.getId());
        assertEquals("Fayre", person.getFirstName());
        assertEquals("Sprowle", person.getLastName());
        assertEquals("3066 Lakewood Plaza", person.getAddress());
        assertEquals("Female", person.getGender());
    }
    @Test
    @Order(2)
    public void disablePerson(){
        repository.disablePerson(person.getId());

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC,"firstName"));
        person = repository.findPersonByFirstName("ayr", pageable).getContent().getFirst();

        assertFalse(person.getEnabled());

        assertEquals(821 ,person.getId());
        assertEquals("Fayre", person.getFirstName());
        assertEquals("Sprowle", person.getLastName());
        assertEquals("3066 Lakewood Plaza", person.getAddress());
        assertEquals("Female", person.getGender());
    }


}
