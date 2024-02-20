package com.example.restwithspringboot.services;

import com.example.restwithspringboot.controller.BookController;
import com.example.restwithspringboot.controller.PersonController;
import com.example.restwithspringboot.data.vo.v1.BookVO;
import com.example.restwithspringboot.data.vo.v1.PersonVO;
import com.example.restwithspringboot.exceptions.RequiredObjectIsNullException;
import com.example.restwithspringboot.exceptions.ResourceNotFoundException;
import com.example.restwithspringboot.mapper.DozerMapper;
import com.example.restwithspringboot.model.Book;
import com.example.restwithspringboot.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BookServices {
    private final AtomicLong counter = new AtomicLong();
    private Logger logger = Logger.getLogger(BookVO.class.getName());

    @Autowired
    BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookVO> assembler;

    public BookVO findById(Long id){
        logger.info("Finding one book!");
        Book entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        BookVO vo = parseObject(entity, BookVO.class);
        vo.add(
                linkTo(
                        methodOn(BookController.class).findById(id)).withSelfRel()
        );
        return vo;
    }

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable){

        var bookPage = repository.findAll(pageable);

        var booksVoPage = bookPage.map(b -> DozerMapper.parseObject(b, BookVO.class));
        booksVoPage.map(b -> b.add(
                linkTo(
                        methodOn(BookController.class).findById(b.getKey())).withSelfRel()
        ));

        Link link = linkTo(methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "ASC")).withSelfRel();
        return assembler.toModel(booksVoPage, link);
    }


    public BookVO create(BookVO book){
        if (book == null) throw new RequiredObjectIsNullException();

        Book entity = parseObject(book, Book.class);

        BookVO vo = parseObject(repository.save(entity), BookVO.class);

        vo.add(
                linkTo(
                        methodOn(BookController.class).findById(vo.getKey())).withSelfRel()
        );
        return vo;
    }

    public BookVO update(BookVO book){

        Book entity = repository.findById(book.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setAuthor(book.getAuthor());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());
        entity.setLaunch_date(book.getLaunch_date());

        BookVO vo = parseObject(repository.save(entity), BookVO.class);
        vo.add(
                linkTo(
                        methodOn(BookController.class).findById(vo.getKey())).withSelfRel()
        );
        return vo;
    }

    public void delete(Long id){

        Book entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);

    }
}
