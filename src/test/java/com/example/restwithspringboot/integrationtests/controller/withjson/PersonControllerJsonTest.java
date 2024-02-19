package com.example.restwithspringboot.integrationtests.controller.withjson;

import com.example.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.restwithspringboot.integrationtests.vo.AccountCredentialsVO;
import com.example.restwithspringboot.integrationtests.vo.PersonVO;
import com.example.restwithspringboot.integrationtests.vo.TokenVO;
import com.example.restwithspringboot.integrationtests.vo.wrappers.WrapperPersonVO;
import configs.TestConfigs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static PersonVO person;

    @BeforeAll
    public static void setup(){
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        person = new PersonVO();

    }

    @Order(0)
    @Test
    public void authorization() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("gurgel", "admin123");
        var accessToken = given()
                .basePath("/auth/signin")
                    .port(TestConfigs.SERVER_PORT)
                    .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
        assertFalse(persistedPerson.getEnabled());

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
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
            .queryParams("page", 3, "size", 10, "direction", "asc")
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .queryParams("page", 0, "size", 10, "direction", "asc")
                        .body(person)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
        var people = wrapper.getEmbedded().getPersons();

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
            .contentType(TestConfigs.CONTENT_TYPE_JSON)
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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .queryParams("page", 0, "size", 6, "direction", "asc")
                        .pathParam("firstName", "ayr")
                        .when()
                        .get("/findPersonByFirstName/{firstName}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();

        WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
        var people = wrapper.getEmbedded().getPersons();

        PersonVO foundPersonOne = people.getFirst();

        assertFalse(foundPersonOne.getEnabled());

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
                        .contentType(TestConfigs.CONTENT_TYPE_JSON)
                        .queryParams("page", 0, "size", 10, "direction", "asc")
                        .body(person)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .asString();



        assertTrue(content.contains("_links\":{\"self\":{\"href\":\"http://localhost:8888/person/372\"}}}"));
        assertTrue(content.contains("_links\":{\"self\":{\"href\":\"http://localhost:8888/person/79\"}}}"));
        assertTrue(content.contains("_links\":{\"self\":{\"href\":\"http://localhost:8888/person/467\"}}}"));

        assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":1004,\"totalPages\":101,\"number\":0}}"));
        assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/person?direction=ASC&page=100&size=10&sort=firstName,asc\"}}"));
        assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/person?direction=ASC&page=1&size=10&sort=firstName,asc\"}"));
        assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/person?page=0&size=10&direction=ASC\"}"));
        assertTrue(content.contains("\"first\":{\"href\":\"http://localhost:8888/person?direction=ASC&page=0&size=10&sort=firstName,asc\"}"));
    }

    private void mockPerson() {
        person.setFirstName("Nelson");
        person.setLastName("Piquet");
        person.setAddress("Brasília - DF - Brasil");
        person.setGender("Male");
        person.setEnabled(true);
    }


}
