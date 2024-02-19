package com.example.restwithspringboot.services;

import com.example.restwithspringboot.controller.PersonController;
import com.example.restwithspringboot.data.vo.v1.PersonVO;
import com.example.restwithspringboot.exceptions.RequiredObjectIsNullException;
import com.example.restwithspringboot.exceptions.ResourceNotFoundException;
import com.example.restwithspringboot.mapper.DozerMapper;
import com.example.restwithspringboot.model.Person;
import com.example.restwithspringboot.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.example.restwithspringboot.mapper.DozerMapper.parseObject;

@Service
public class PersonServices {
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(PersonVO.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;

    public PersonVO findById(Long id){
        logger.info("Finding one person!");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        PersonVO vo = parseObject(entity, PersonVO.class);
        vo.add(
                linkTo(
                        methodOn(PersonController.class).findById(id)).withSelfRel()
        );
        return vo;
    }

    public PagedModel<EntityModel<PersonVO>> findAll(Pageable pageable){

        var personPage = repository.findAll(pageable);

        var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVosPage.map(p -> p.add(
                linkTo(
                        methodOn(PersonController.class).findById(p.getKey())).withSelfRel()
        ));

        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "ASC")).withSelfRel();
        return assembler.toModel(personVosPage, link);
    }

    public PagedModel<EntityModel<PersonVO>> findPersonByFirstName(String firstName, Pageable pageable){
        var personPage = repository.findPersonByFirstName(firstName, pageable);

        var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
        personVosPage.map(p -> p.add(
                linkTo(
                        methodOn(PersonController.class).findById(p.getKey())).withSelfRel()
        ));

        Link link = linkTo(methodOn(PersonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "ASC")).withSelfRel();
        return assembler.toModel(personVosPage, link);
    }

    public PersonVO create(PersonVO person){
        if (person == null) throw new RequiredObjectIsNullException();

        Person entity = parseObject(person, Person.class);

        PersonVO vo = parseObject(repository.save(entity), PersonVO.class);

        vo.add(
                linkTo(
                        methodOn(PersonController.class).findById(vo.getKey())).withSelfRel()
        );
        return vo;
    }

    public PersonVO update(PersonVO person){

        Person entity = repository.findById(person.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setGender(person.getGender());
        entity.setAddress(person.getAddress());
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());

        PersonVO vo = parseObject(repository.save(entity), PersonVO.class);
        vo.add(
                linkTo(
                        methodOn(PersonController.class).findById(vo.getKey())).withSelfRel()
        );
        return vo;
    }

    @Transactional
    public PersonVO disablePerson(Long id){
        logger.info("Disabling one person!");
        repository.disablePerson(id);

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        PersonVO vo = parseObject(entity, PersonVO.class);
        vo.add(
                linkTo(
                        methodOn(PersonController.class).findById(id)).withSelfRel()
        );
        return vo;
    }

    public void delete(Long id){

        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);

    }
}
