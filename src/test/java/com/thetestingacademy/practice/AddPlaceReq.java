package com.thetestingacademy.practice;

import com.thetestingacademy.PojoClasses.AddPlace;
import com.thetestingacademy.PojoClasses.Locationn;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;

public class AddPlaceReq {
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
        Response res = given().baseUri("https://rahulshettyacademy.com")
                .queryParam("key","qaclick123")
                .body(addP)
                .when().post("/maps/api/place/add/json")
                .then().assertThat().statusCode(200).extract().response();
        System.out.println(res.asString());
        JsonPath json = new JsonPath(res.asString());
        Assert.assertTrue(json.getString("status").equals("OK"));
    }
}
