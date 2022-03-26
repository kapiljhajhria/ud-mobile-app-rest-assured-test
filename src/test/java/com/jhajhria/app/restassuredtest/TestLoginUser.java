package com.jhajhria.app.restassuredtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestLoginUser {

    private final String CONTEXT_PATH = "/photo-app";
    private final String JSON = "application/json";
    private static String authorizationHeader;
    private static String userId;
    private static List<Map<String, String>> addresses;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
        RestAssured.basePath =CONTEXT_PATH + "/api/v1";
        System.out.println("Before each test");
    }

    @Test
    @Order(1)
    @DisplayName("When correct credentials are provided, user should be able to login")
    public void testUserLogin() throws JsonProcessingException {
        // need to update db manually in order for this test to pass as we can't verify email automatically
        String reqBodyString = "{\n" +
                "    \"username\":\"jhajhria44@gmail.com\",\n" +
                "    \"password\":\"password\"\n" +
                "}";
        var requestBody = new ObjectMapper().readValue(reqBodyString, HashMap.class);

        Response response =  given().
                contentType("application/json").
                accept("application/json").
                body(requestBody).
                when().
                post("/login").
                then().
                statusCode(200).
                extract()
                .response();

        System.out.println(response.asString());

         authorizationHeader = response.getHeader("Authorization");
         userId = response.getHeader("UserId");

        assertNotNull(authorizationHeader);
        assertNotNull(userId);

    }

    @Test
    @Order(2)
    @DisplayName("After successful login, user should be able to access protected resources")
    public void testGettingUserDetailsAFterSuccessfulLogin()   {
        Response response =  given().
                accept(JSON).
                header("Authorization", authorizationHeader).
                when()
                .get("/users/"+userId).
                then().
                statusCode(200).
                contentType(JSON).extract().response();

        String userPublicId = response.jsonPath().getString("userId");
        String userEmail = response.jsonPath().getString("email");
        String firstname = response.jsonPath().getString("firstName");
        String lastname = response.jsonPath().getString("lastName");
        addresses = response.jsonPath().getList("addresses");
//        String addressId = addresses.get(0).get("addressId");

        assertNotNull(userEmail);
        assertNotNull(firstname);
        assertNotNull(lastname);
//        Assertions.assertNotNull(addresses);
//        Assertions.assertEquals(2,addresses.size());
//        Assertions.assertEquals(30,addressId.length());
        assertNotNull(userId);
        assertEquals(userId, userPublicId);



    }

    @Test
    @Order(3)
    @DisplayName("Update user details")
    public void testUpdateUserDetails() throws JsonProcessingException {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "john");
        userDetails.put("lastName", "doe");

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .header("Authorization", authorizationHeader)
                .pathParam("id", userId)
                .body(userDetails)
                .when()
                .put( "/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");

        List<Map<String, String>> storedAddresses = response.jsonPath().getList("addresses");

        assertEquals("john", firstName);
        assertEquals("doe", lastName);
//        assertNotNull(storedAddresses);
//        assertTrue(addresses.size() == storedAddresses.size());
//        assertEquals(addresses.get(0).get("streetName"), storedAddresses.get(0).get("streetName"));

    }

    @Test
    @Order(4)
    @DisplayName("Delete user")
    public void testDeleteUser(){

        Response response = given()
                .header("Authorization",authorizationHeader)
                .accept(JSON)
                .pathParam("id", userId)
                .when()
                .delete("/users/{id}")
                .then()
                .statusCode(200)
                .contentType(JSON)
                .extract()
                .response();

        String operationResult = response.jsonPath().getString("operationResult");
        assertEquals("SUCCESS", operationResult);

    }


}