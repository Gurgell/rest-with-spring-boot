package com.example.restwithspringboot.mapper.custom;

import com.example.restwithspringboot.data.vo.v1.PersonVO;
import com.example.restwithspringboot.data.vo.v2.PersonVOV2;
import com.example.restwithspringboot.model.Person;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Bean;

import java.util.Date;

public class PersonMapper {

    private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
    public static PersonVO convertEntityToVo(Person person){
        PersonVO vo = new PersonVO();
        vo.setKey(person.getId());
        vo.setAddress(person.getAddress());
        //vo.setBirthDay(new Date());
        vo.setFirstName(person.getFirstName());
        vo.setLastName(person.getLastName());
        vo.setGender(person.getGender());

        return vo;
    }

    public static Person convertVoToEntity(PersonVO person){
        Person entity = new Person();
        entity.setId(person.getKey());
        entity.setAddress(person.getAddress());
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setGender(person.getGender());

        return entity;
    }
}
