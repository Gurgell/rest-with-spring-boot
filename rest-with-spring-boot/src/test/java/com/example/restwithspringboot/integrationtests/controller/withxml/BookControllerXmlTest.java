package com.example.restwithspringboot.integrationtests.controller.withxml;

import com.example.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.restwithspringboot.integrationtests.vo.AccountCredentialsVO;
import com.example.restwithspringboot.integrationtests.vo.BookVO;
import com.example.restwithspringboot.integrationtests.vo.TokenVO;
import com.example.restwithspringboot.integrationtests.vo.pagedmodels.PagedModelBook;
import com.example.restwithspringboot.integrationtests.vo.pagedmodels.PagedModelPerson;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import configs.TestConfigs;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static XmlMapper xmlMapper;
    private static BookVO book;

    @BeforeAll
    public static void setup(){
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        xmlMapper = new XmlMapper();
        xmlMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        book = new BookVO();

    }

    @Order(0)
    @Test
    public void authorization() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("gurgel", "admin123");
        var accessToken = given()
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_XML)
                .body(user)
                    .when()
                .post()
                    .then()
                        .statusCode(200)
                            .extract()
                            .body()
                                .as(TokenVO.class)
                            .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/book")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Order(1)
    @Test
    public void testCreate() throws IOException, ParseException {
        mockBook();

        var content =
            given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                    .body(book)
                    .when()
                    .post()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();
        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        book = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunch_date());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getTitle());

        assertTrue(persistedBook.getId() > 0);
        
        assertEquals("Fulano de tal", persistedBook.getAuthor());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        assertEquals(dateFormat.parse("2023-11-01 17:03:06.674000"), persistedBook.getLaunch_date());
        assertEquals(35.00, persistedBook.getPrice());
        assertEquals("Head First Design Patterns", persistedBook.getTitle());
    }

    @Order(2)
    @Test
    public void testUpdate() throws IOException, ParseException {
        book.setTitle("Design Patterns");

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .body(book)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        book = persistedBook;

        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunch_date());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getTitle());

        assertEquals(book.getId(), persistedBook.getId());

        assertEquals("Fulano de tal", persistedBook.getAuthor());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        assertEquals(dateFormat.parse("2023-11-01 17:03:06.674000"), persistedBook.getLaunch_date());
        assertEquals(35.00, persistedBook.getPrice());
        assertEquals("Design Patterns", persistedBook.getTitle());
    }

    @Order(3)
    @Test
    public void testFindById() throws IOException, ParseException {
        mockBook();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GURGEL)
                    .pathParam("id", book.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                        .extract()
                            .body()
                                .asString();
        BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
        book = persistedBook;

        assertEquals(book.getId(), persistedBook.getId());

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getId());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunch_date());
        assertNotNull(persistedBook.getPrice());
        assertNotNull(persistedBook.getTitle());

        assertTrue(persistedBook.getId() > 0);

        assertEquals("Fulano de tal", persistedBook.getAuthor());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        assertEquals(dateFormat.parse("2023-11-01 17:03:06.674000"), persistedBook.getLaunch_date());
        assertEquals(35.00, persistedBook.getPrice());
        assertEquals("Design Patterns", persistedBook.getTitle());
    }

    @Order(4)
    @Test
    public void testDelete() throws IOException {
        given().spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .pathParam("id", book.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }

    @Order(5)
    @Test
    public void testFindAll() throws IOException, ParseException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page", 0, "size", 10, "direction", "asc", "mediaType", TestConfigs.CONTENT_TYPE_XML_VIA_HEADER_PARAM)
                        .body(book)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        PagedModelBook wrapper = xmlMapper.readValue(content, PagedModelBook.class);

        var books = wrapper.getContent();

        BookVO foundBookOne = books.getFirst();

        assertNotNull(foundBookOne.getId());
        assertNotNull(foundBookOne.getAuthor());
        assertNotNull(foundBookOne.getLaunch_date());
        assertNotNull(foundBookOne.getPrice());
        assertNotNull(foundBookOne.getTitle());

        assertEquals(12 ,foundBookOne.getId());

        assertEquals("Viktor Mayer-Schonberger e Kenneth Kukier", foundBookOne.getAuthor());
        assertEquals(Date.from(Instant.parse("2017-11-07T17:09:01.674+00:00")), foundBookOne.getLaunch_date());
        assertEquals(54.0, foundBookOne.getPrice());
        assertEquals("Big Data: como extrair volume, variedade, velocidade e valor da avalanche de informação cotidiana", foundBookOne.getTitle());

        BookVO foundBookTwo = books.get(1);

        assertNotNull(foundBookTwo.getId());
        assertNotNull(foundBookTwo.getAuthor());
        assertNotNull(foundBookTwo.getLaunch_date());
        assertNotNull(foundBookTwo.getPrice());
        assertNotNull(foundBookTwo.getTitle());

        assertEquals(3 ,foundBookTwo.getId());

        assertEquals("Robert C. Martin", foundBookTwo.getAuthor());

        assertEquals(Date.from(Instant.parse("2009-01-10T02:00:00.000+00:00")), foundBookTwo.getLaunch_date());
        assertEquals(77.00, foundBookTwo.getPrice());
        assertEquals("Clean Code", foundBookTwo.getTitle());

        BookVO foundLastBook = books.get(2);

        assertNotNull(foundLastBook.getId());
        assertNotNull(foundLastBook.getAuthor());
        assertNotNull(foundLastBook.getLaunch_date());
        assertNotNull(foundLastBook.getPrice());
        assertNotNull(foundLastBook.getTitle());

        assertEquals(5 ,foundLastBook.getId());

        assertEquals("Steve McConnell", foundLastBook.getAuthor());
        assertEquals(Date.from(Instant.parse("2017-11-07T17:09:01.674+00:00")), foundLastBook.getLaunch_date());
        assertEquals(58.00, foundLastBook.getPrice());
        assertEquals("Code complete", foundLastBook.getTitle());
    }

    @Order(6)
    @Test
    public void testFindAllWithoutToken() throws IOException {
        RequestSpecification specificationWithoutToken =  new RequestSpecBuilder()
            .setBasePath("/book")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        given().spec(specificationWithoutToken)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .body(book)
            .when()
            .get()
            .then()
            .statusCode(403);
    }

    @Order(7)
    @Test
    public void testHateos() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page", 0, "size", 10, "direction", "asc", "mediaType", TestConfigs.CONTENT_TYPE_XML_VIA_HEADER_PARAM)
                        .body(book)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();


        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/book/12</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/book/3</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/book/8</href></links>"));

        assertTrue(content.contains("<page><size>10</size><totalElements>15</totalElements><totalPages>2</totalPages><number>0</number></page>"));
        assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/book?direction=ASC&amp;page=1&amp;size=10&amp;sort=title,asc</href></links>"));
        assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/book?direction=ASC&amp;page=1&amp;size=10&amp;sort=title,asc</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/book?page=0&amp;size=10&amp;direction=ASC</href></links>"));
        assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/book?direction=ASC&amp;page=0&amp;size=10&amp;sort=title,asc</href></links>"));
    }

    private void mockBook() throws ParseException {
        book.setAuthor("Fulano de tal");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        book.setLaunch_date(dateFormat.parse("2023-11-01 17:03:06.674000"));
        book.setPrice(35.00);
        book.setTitle("Head First Design Patterns");
    }
}
