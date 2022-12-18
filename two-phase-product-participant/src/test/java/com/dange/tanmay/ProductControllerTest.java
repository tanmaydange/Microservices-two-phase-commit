package com.dange.tanmay;

import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProductService.class)
@WebAppConfiguration
@IntegrationTest({"server.port:0",
        "spring.datasource.url:jdbc:h2:mem:two-phase-cororididator;DB_CLOSE_ON_EXIT=FALSE"})
public class ProductControllerTest {
    @Value("${local.server.port}")
    int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
    }

    @Test
    public void testPrepareValidQuantity() throws Exception {
        given().param("txnId", 1)
                .param("product", "apple")
                .param("quantity", 20)
                .get("/prepare")
                .then()
                .body(is("True"));
    }


    @Test
    public void testPrepareGreaterQuantity() throws Exception {
        given().param("txnId", 1)
                .param("product", "apple")
                .param("quantity", 20000)
                .get("/prepare")
                .then()
                .body(is("False"));
    }


    @Test
    public void testCommit() throws Exception {
        given().param("txnId", 1)
                .get("/commit")
                .then()
                .body(is("True"));
    }


     @Test
    public void testPrepareValidQuantityForRollback() throws Exception {
        given().param("txnId", 2)
                .param("product", "apple")
                .param("quantity", 30)
                .get("/prepare")
                .then()
                .body(is("True"));
    }

    @Test
    public void testRollback() throws Exception {
        given().param("txnId", 2)
                .get("/rollback")
                .then()
                .body(is("False"));
    }
   
}