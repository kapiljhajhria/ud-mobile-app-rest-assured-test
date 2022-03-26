package com.jhajhria.app.restassuredtest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class TestCreateUser {

    private final String CONTEXT_PATH = "/photo-app";

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
        RestAssured.basePath = CONTEXT_PATH +"/api/v1/users";

    }

    @Test
    final void test() throws JSONException, JsonProcessingException {
        String jsonString = "{\n" +
                "    \"firstName\":\"john\",\n" +
                "    \"lastName\":\"doe\",\n" +
                "    \"email\":\"jhajhria44@gmail.com\",\n" +
                "    \"password\":\"password\",\n" +
                "    \"addresses\":[\n" +
                "        {\n" +
                "            \"city\":\"delhi\",\n" +
                "            \"country\":\"India\",\n" +
                "            \"streetName\":\"H.N. 123 Street happy\",\n" +
                "            \"postalCode\":\"122002\",\n" +
                "            \"type\":\"billing\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"city\":\"Rewari\",\n" +
                "            \"country\":\"India\",\n" +
                "            \"streetName\":\"H.N. 2233 Street work\",\n" +
                "            \"postalCode\":\"122002\",\n" +
                "            \"type\":\"shipping\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

       // convert jsonString to JSONObject
//        Map<String, Object> retMap = new Gson().fromJson(
//                jsonString, new TypeToken<HashMap<String, Object>>() {}.getType()
//        );

        Map<String, Object> requestBody = new ObjectMapper().readValue(jsonString, HashMap.class);


        Response response =  given().
                contentType("application/json").
                accept("application/json").
                body(requestBody).
                when().
                post("").
                then().
                statusCode(201).
                contentType("application/json").
                extract()
                .response();

        String userId = response.jsonPath().getString("userId");

        Assertions.assertNotNull(userId);


        //convert response to JSONObject
        JSONObject responseJsonObject = new JSONObject(response.asString());

        JSONArray addresses = responseJsonObject.getJSONArray("addresses");

        Assertions.assertNotNull(addresses);
        Assertions.assertEquals(2, addresses.length());

        String addressId = addresses.getJSONObject(0).getString("addressId");

        Assertions.assertNotNull(addressId);
        Assertions.assertEquals(30, addressId.length());




    }
}