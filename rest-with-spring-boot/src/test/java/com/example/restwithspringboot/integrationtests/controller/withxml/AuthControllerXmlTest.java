package com.example.restwithspringboot.integrationtests.controller.withxml;

import com.example.restwithspringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import com.example.restwithspringboot.integrationtests.vo.AccountCredentialsVO;
import com.example.restwithspringboot.integrationtests.vo.TokenVO;
import configs.TestConfigs;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerXmlTest extends AbstractIntegrationTest {
    private static TokenVO tokenVO;

    @Order(1)
    @Test
    public void testSignin() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("gurgel", "admin123");
        tokenVO = given()
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
                .as(TokenVO.class);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Order(2)
    @Test
    public void testeRefresh() throws IOException {
        AccountCredentialsVO user = new AccountCredentialsVO("gurgel", "admin123");
        TokenVO newTokenVO = given()
            .basePath("/auth/refresh")
            .port(TestConfigs.SERVER_PORT)
            .contentType(TestConfigs.CONTENT_TYPE_XML)
                .pathParam("username", tokenVO.getUsername())
                .header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
            .when()
                .put("{username}")
            .then()
                .statusCode(200)
            .extract()
                .body()
                    .as(TokenVO.class);

        assertNotNull(newTokenVO.getAccessToken());
        assertNotNull(newTokenVO.getRefreshToken());
    }
}
