package com.thetestingacademy.practice;

import com.thetestingacademy.PojoClasses.AddPlace;
import com.thetestingacademy.PojoClasses.Locationn;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;

import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class RequestResponseSpecification {
    public static void main(String[] args) {
        //Request Body Serialization using POJO
        Locationn loc = new Locationn();
        loc.setLat(-38.383494);
        loc.setLng(33.427362);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("shoe park");
        arrayList.add("shop");
        AddPlace addP = new AddPlace();
        addP.setAccuracy(50);
        addP.setAddress("29, side layout, cohen 09");
        addP.setLanguage("French-IN");
        addP.setLocation(loc);
        addP.setName("Frontline house");
        addP.setTypes(arrayList);
        addP.setWebsite("http://google.com");
        addP.setPhone_number("(+91) 983 893 3937");
        RequestSpecification reqSpec = new RequestSpecBuilder().setBaseUri("https://rahulshettyacademy.com")
                .addQueryParam("key","qaclick123").setContentType(ContentType.JSON)
                .build();
        ResponseSpecification resSpec = new ResponseSpecBuilder().expectStatusCode(200)
                .expectContentType(ContentType.JSON).build();

        Response res = given().spec(reqSpec).body(addP).when().put("/maps/api/place/add/json")
                .then().spec(resSpec).extract().response();
        System.out.println(res.asString());
        JsonPath json = new JsonPath(res.asString());
        Assert.assertTrue(json.getString("status").equals("OK"));
    }
}
