package com.example.restwithspringboot.unittests.mockito.services;

import com.example.restwithspringboot.data.vo.v1.BookVO;
import com.example.restwithspringboot.exceptions.RequiredObjectIsNullException;
import com.example.restwithspringboot.model.Book;
import com.example.restwithspringboot.repositories.BookRepository;
import com.example.restwithspringboot.services.BookServices;
import com.example.restwithspringboot.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {
//
//    MockBook input;
//    @InjectMocks
//    private BookServices service;
//
//    @Mock
//    BookRepository bookRepository;
//
//    @BeforeEach
//    void setUpMocks() throws Exception{
//        input = new MockBook();
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testFindById() {
//        Book entity = input.mockEntity(1);
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));
//        var result = service.findById(1L);
//
//        assertNotNull(result);
//        assertNotNull(result.getKey());
//        assertNotNull(result.getLinks());
//        System.out.println(result);
//        assertTrue(result.toString().contains("links: [</book/1>;rel=\"self\"]"));
//        assertEquals("Author Test1", result.getAuthor());
//        assertEquals("Title Test1", result.getTitle());
//        assertEquals("Thu Jan 27 00:00:00 BRT 2022", result.getLaunch_date().toString());
//        assertEquals(3001.0, result.getPrice());
//    }
////    Book book = new Book();
////        book.setLaunch_date(new Date(2024, 0, 26 + number));
////        book.setTitle("Title Test" + number);
////        book.setPrice(3000.0 + number);
////        book.setId(number.longValue());
////        book.setAuthor("Author Test" + number);
////        return book;
//
//    @Test
//    void testFindAll(){
//        List<Book> list = input.mockEntityList();
//
//        when(bookRepository.findAll()).thenReturn(list);
//        var books = service.findAll(pageable);
//
//        assertNotNull(books);
//        assertEquals(14, books.size());
//
//        var bookOne = books.get(1);
//
//        assertNotNull(bookOne);
//        assertNotNull(bookOne.getKey());
//        assertNotNull(bookOne.getLinks());
//        assertTrue(bookOne.toString().contains("links: [</book/1>;rel=\"self\"]"));
//        assertEquals("Author Test1", bookOne.getAuthor());
//        assertEquals("Title Test1", bookOne.getTitle());
//        assertEquals("Thu Jan 27 00:00:00 BRT 2022", bookOne.getLaunch_date().toString());
//        assertEquals(3001.0, bookOne.getPrice());
//
//        var bookFour = books.get(4);
//
//        assertNotNull(bookFour);
//        assertNotNull(bookFour.getKey());
//        assertNotNull(bookFour.getLinks());
//        assertTrue(bookFour.toString().contains("links: [</book/4>;rel=\"self\"]"));
//        assertEquals("Author Test4", bookFour.getAuthor());
//        assertEquals("Title Test4", bookFour.getTitle());
//        assertEquals("Sun Jan 30 00:00:00 BRT 2022", bookFour.getLaunch_date().toString());
//        assertEquals(3004.0, bookFour.getPrice());
//
//        var bookSeven = books.get(7);
//
//        assertNotNull(bookSeven);
//        assertNotNull(bookSeven.getKey());
//        assertNotNull(bookSeven.getLinks());
//        assertTrue(bookSeven.toString().contains("links: [</book/7>;rel=\"self\"]"));
//        assertEquals("Author Test7", bookSeven.getAuthor());
//        assertEquals("Title Test7", bookSeven.getTitle());
//        assertEquals("Wed Feb 02 00:00:00 BRT 2022", bookSeven.getLaunch_date().toString());
//        assertEquals(3007.0, bookSeven.getPrice());
//
//
//    }
//    @Test
//    void testDelete() {
//        Book entity = input.mockEntity(1);
//        entity.setId(1L);
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));
//
//        service.delete(1L);
//    }
//
//    @Test
//    void testCreate(){
//
//        Book entity = input.mockEntity(1);
//        Book persisted = entity;
//        persisted.setId(1L);
//
//        BookVO vo = input.mockVO(1);
//        vo.setKey(1L);
//
//        when(bookRepository.findById(1L)).thenReturn(Optional.of(entity));
//        when(bookRepository.save(entity)).thenReturn(persisted);
//
//        var result = service.update(vo);
//
//        assertNotNull(result);
//        assertNotNull(result.getKey());
//        assertNotNull(result.getLinks());
//        assertTrue(result.toString().contains("links: [</book/1>;rel=\"self\"]"));
//        assertEquals("Author Test1", result.getAuthor());
//        assertEquals("Title Test1", result.getTitle());
//        assertEquals("Thu Jan 27 00:00:00 BRT 2022", result.getLaunch_date().toString());
//        assertEquals(3001.0, result.getPrice());
//    }
//
//    @Test
//    void testCreateWithNullBook(){
//        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
//            service.create(null);
//        });
//        String expectedMessage = "It is not allowed to persist a null object";
//        String actualMessage = exception.getMessage();
//
//        assertTrue(actualMessage.contains(expectedMessage));
//    }
//
//    @Test
//    void testUpdate(){
//        Book entity = input.mockEntity(1);
//        Book persisted = entity;
//        persisted.setId(1L);
//
//        BookVO vo = input.mockVO(1);
//        vo.setKey(1L);
//
//        when(bookRepository.save(entity)).thenReturn(persisted);
//
//        var result = service.create(vo);
//
//        assertNotNull(result);
//        assertNotNull(result.getKey());
//        assertNotNull(result.getLinks());
//        assertTrue(result.toString().contains("links: [</book/1>;rel=\"self\"]"));
//        assertEquals("Author Test1", result.getAuthor());
//        assertEquals("Title Test1", result.getTitle());
//        assertEquals("Thu Jan 27 00:00:00 BRT 2022", result.getLaunch_date().toString());
//        assertEquals(3001.0, result.getPrice());
//    }
//
//    @Test
//    void testUpdateWithNullBook(){
//        Exception exception = assertThrows(NullPointerException.class, () -> {
//            service.update(null);
//        });
//    }
}