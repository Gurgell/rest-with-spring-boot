package com.example.restwithspringboot.services;

import com.example.restwithspringboot.controller.PersonController;
import com.example.restwithspringboot.data.vo.v1.PersonVO;
import com.example.restwithspringboot.data.vo.v1.security.AccountCredentialsVO;
import com.example.restwithspringboot.exceptions.RequiredObjectIsNullException;
import com.example.restwithspringboot.exceptions.ResourceNotFoundException;
import com.example.restwithspringboot.mapper.DozerMapper;
import com.example.restwithspringboot.model.Person;
import com.example.restwithspringboot.repositories.PersonRepository;
import com.example.restwithspringboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.example.restwithspringboot.mapper.DozerMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserServices implements UserDetailsService {
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(AccountCredentialsVO.class.getName());

    @Autowired
    UserRepository repository;

    public UserServices(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Finding one user by name!" + username);
        var user = repository.findByUsername(username);

        if (user != null){
            return user;
        }else{
            throw new UsernameNotFoundException("Username: " + username + "not found!");
        }
    }
}
