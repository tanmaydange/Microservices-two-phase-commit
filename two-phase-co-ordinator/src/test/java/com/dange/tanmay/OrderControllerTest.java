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
@SpringApplicationConfiguration(classes = OrderService.class)
@WebAppConfiguration
@IntegrationTest({"server.port:0",
        "spring.datasource.url:jdbc:h2:mem:two-phase-cororididator;DB_CLOSE_ON_EXIT=FALSE"})
public class OrderControllerTest {
    @Value("${local.server.port}")
    int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
    }
 
 
    @Test
    public void testCalc() throws Exception {
        given().param("accountName", "jainesh")
                .param("product", "apple")
                .param("quantity", 10)
                .get("/createOrder")
                .then()
                .body(is("{ status : Transaction Complete,Customer_Name :jainesh,Product : apple,Quantity : 10,Total : 200}"));
    }
}