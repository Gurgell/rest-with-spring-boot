package com.example.restwithspringboot.integrationtests.controller.withxml;

import com.example.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.restwithspringboot.integrationtests.vo.AccountCredentialsVO;
import com.example.restwithspringboot.integrationtests.vo.PersonVO;
import com.example.restwithspringboot.integrationtests.vo.TokenVO;
import com.example.restwithspringboot.integrationtests.vo.pagedmodels.PagedModelPerson;
import com.example.restwithspringboot.integrationtests.vo.wrappers.WrapperPersonVO;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static XmlMapper xmlMapper;
    private static PersonVO person;

    @BeforeAll
    public static void setup(){
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        xmlMapper = new XmlMapper();
        xmlMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();

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
                .setBasePath("/person")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();
    }

    @Order(1)
    @Test
    public void testCreate() throws IOException {
        mockPerson();

        var content =
            given().spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_XML)
                    .body(person)
                    .when()
                    .post()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
                        .asString();
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertTrue(persistedPerson.getEnabled());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet", persistedPerson.getLastName());
        assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Order(2)
    @Test
    public void testUpdate() throws IOException {
        person.setLastName("Piquet Souto Maior");

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .body(person)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertTrue(persistedPerson.getEnabled());

        assertEquals(person.getId(), persistedPerson.getId());

        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
        assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Order(3)
    @Test
    public void testDisableFindById() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .pathParam("id", person.getId())
                        .when()
                        .patch("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertEquals(person.getId(), persistedPerson.getId());

        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());
        assertFalse(persistedPerson.getEnabled());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
        assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Order(4)
    @Test
    public void testFindById() throws IOException {
        mockPerson();

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_GURGEL)
                    .pathParam("id", person.getId())
                .when()
                    .get("{id}")
                .then()
                    .statusCode(200)
                        .extract()
                            .body()
                                .asString();
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        person = persistedPerson;

        assertEquals(person.getId(), persistedPerson.getId());

        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getGender());

        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Nelson", persistedPerson.getFirstName());
        assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
        assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
        assertEquals("Male", persistedPerson.getGender());
    }

    @Order(5)
    @Test
    public void testDelete() throws IOException {
        given().spec(specification)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .pathParam("id", person.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }

    @Order(6)
    @Test
    public void testFindAll() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page", 0, "size", 10, "direction", "asc", "mediaType", TestConfigs.CONTENT_TYPE_XML_VIA_HEADER_PARAM)
                        .body(person)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        PagedModelPerson wrapper = xmlMapper.readValue(content, PagedModelPerson.class);
        var people = wrapper.getContent();

        PersonVO foundPersonOne = people.getFirst();

        assertNotNull(foundPersonOne.getFirstName());
        assertNotNull(foundPersonOne.getLastName());
        assertNotNull(foundPersonOne.getAddress());
        assertNotNull(foundPersonOne.getGender());
        assertFalse(foundPersonOne.getEnabled());

        assertEquals(372, foundPersonOne.getId());

        assertEquals("Abeu", foundPersonOne.getFirstName());
        assertEquals("Gino", foundPersonOne.getLastName());
        assertEquals("3 Sachs Plaza", foundPersonOne.getAddress());
        assertEquals("Male", foundPersonOne.getGender());

        PersonVO foundPersonTwo = people.get(1);

        assertNotNull(foundPersonTwo.getFirstName());
        assertNotNull(foundPersonTwo.getLastName());
        assertNotNull(foundPersonTwo.getAddress());
        assertNotNull(foundPersonTwo.getGender());
        assertFalse(foundPersonOne.getEnabled());

        assertEquals(79, foundPersonTwo.getId());

        assertEquals("Abigale", foundPersonTwo.getFirstName());
        assertEquals("Kibblewhite", foundPersonTwo.getLastName());
        assertEquals("3433 Maryland Way", foundPersonTwo.getAddress());
        assertEquals("Female", foundPersonTwo.getGender());

        PersonVO foundPersonThree = people.get(2);

        assertNotNull(foundPersonThree.getId());
        assertNotNull(foundPersonThree.getFirstName());
        assertNotNull(foundPersonThree.getLastName());
        assertNotNull(foundPersonThree.getAddress());
        assertNotNull(foundPersonThree.getGender());
        assertFalse(foundPersonThree.getEnabled());

        assertEquals(122, foundPersonThree.getId());

        assertEquals("Abran", foundPersonThree.getFirstName());
        assertEquals("Lutzmann", foundPersonThree.getLastName());
        assertEquals("085 Sugar Circle", foundPersonThree.getAddress());
        assertEquals("Male", foundPersonThree.getGender());
    }

    @Order(7)
    @Test
    public void testFindAllWithoutToken() throws IOException {
        RequestSpecification specificationWithoutToken =  new RequestSpecBuilder()
            .setBasePath("/person")
            .setPort(TestConfigs.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        given().spec(specificationWithoutToken)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
            .body(person)
            .when()
            .get()
            .then()
            .statusCode(403);
    }

    @Order(8)
    @Test
    public void testFindByName() throws IOException {

        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page", 0, "size", 6, "direction", "asc", "mediaType", TestConfigs.CONTENT_TYPE_XML_VIA_HEADER_PARAM)
                        .pathParam("firstName", "ayr")
                        .when()
                        .get("/findPersonByFirstName/{firstName}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        PagedModelPerson wrapper = xmlMapper.readValue(content, PagedModelPerson.class);
        var people = wrapper.getContent();

        PersonVO foundPersonOne = people.getFirst();

        assertEquals(821 ,foundPersonOne.getId());

        assertEquals("Fayre", foundPersonOne.getFirstName());
        assertEquals("Sprowle", foundPersonOne.getLastName());
        assertEquals("3066 Lakewood Plaza", foundPersonOne.getAddress());
        assertEquals("Female", foundPersonOne.getGender());
    }

    @Order(9)
    @Test
    public void testHateos() throws IOException {
        var content =
                given().spec(specification)
                        .contentType(TestConfigs.CONTENT_TYPE_XML)
                        .queryParams("page", 0, "size", 10, "direction", "asc", "mediaType", TestConfigs.CONTENT_TYPE_XML_VIA_HEADER_PARAM)
                        .body(person)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();


        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/person/372</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/person/79</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/person/467</href></links>"));

        assertTrue(content.contains("<page><size>10</size><totalElements>1004</totalElements><totalPages>101</totalPages><number>0</number></page>"));
        assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/person?direction=ASC&amp;page=100&amp;size=10&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/person?direction=ASC&amp;page=1&amp;size=10&amp;sort=firstName,asc</href></links>"));
        assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/person?page=0&amp;size=10&amp;direction=ASC</href></links>"));
        assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/person?direction=ASC&amp;page=0&amp;size=10&amp;sort=firstName,asc</href></links>"));
    }

    private void mockPerson() {
        person.setFirstName("Nelson");
        person.setLastName("Piquet");
        person.setAddress("Brasília - DF - Brasil");
        person.setGender("Male");
        person.setEnabled(true);
    }


}
