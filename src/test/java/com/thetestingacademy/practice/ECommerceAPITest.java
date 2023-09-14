package com.thetestingacademy.practice;

import com.thetestingacademy.PojoClasses.CreateOrder;
import com.thetestingacademy.PojoClasses.LoginBody;
import com.thetestingacademy.PojoClasses.LoginResponse;
import com.thetestingacademy.PojoClasses.Order;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ECommerceAPITest {
    public static void main(String[] args) {
        LoginBody user1 = new LoginBody();
        user1.setUserEmail("sanjaysah.engg@gmail.com");
        user1.setUserPassword("APIAutomation@123");
        RequestSpecification reqSpec = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON).build();
        RequestSpecification reqLogin = given().relaxedHTTPSValidation().spec(reqSpec).body(user1);
        ResponseSpecification resSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)//.expectContentType(ContentType.JSON)
                .build();
        LoginResponse loginResponse = reqLogin.when().post("/api/ecom/auth/login")
                .then().log().all().spec(resSpec).extract().response().as(LoginResponse.class);

        String token = loginResponse.getToken();
        String userid = loginResponse.getUserId();

        // Add Product
        RequestSpecification reqCreatePage = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization",token).build();
        RequestSpecification reqCreatePageFormData = given().relaxedHTTPSValidation().spec(reqCreatePage)
                .param("productName","Liberty")
                .param("productAddedBy",userid)
                .param("productCategory","fashion")
                .param("productSubCategory","shoe")
                .param("productPrice","11500")
                .param("productDescription","Addidas Originals")
                .param("productFor","Women")
                .multiPart("productImage",new File("/Users/sanjaykumar/Downloads/shoes1.png"));
        String response = reqCreatePageFormData.when().log().all().post("/api/ecom/product/add-product")
                .then().log().all().extract().response().asString();
        JsonPath js = new JsonPath(response);
        String productId = js.get("productId");

        // Create/Place Order
        Order order = new Order();
        order.setCountry("India");
        order.setProductOrderedId(productId);
        ArrayList<Order> list = new ArrayList<>();
        list.add(order);
        CreateOrder createOrder = new CreateOrder();
        createOrder.setOrders(list);
        RequestSpecification reqcreateOrder = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization",token).setContentType(ContentType.JSON).build();
        RequestSpecification reqCreateOrder1  = given().relaxedHTTPSValidation().spec(reqcreateOrder).body(createOrder);

        String resCreateOrder = reqCreateOrder1.when().post("/api/ecom/order/create-order").then()
                .log().all().extract().response().asString();
        JsonPath jsCO = new JsonPath(resCreateOrder);
        String orders = jsCO.get("orders[0]");
        String productOrderId = jsCO.get("productOrderId[0]");

        // Delete Order
        RequestSpecification reqDelOrder = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("Authorization",token).build();
        RequestSpecification reqDelOrder1 = given().relaxedHTTPSValidation().spec(reqDelOrder)
                .pathParam("productId",productOrderId);

        String resDelOrder = reqDelOrder1.when()
                .delete("/api/ecom/product/delete-product/{productId}")
                .then().log().all().extract().response().asString();
        JsonPath jsDel = new JsonPath(resDelOrder);
        String message = jsDel.get("message");
        Assert.assertTrue(message.equals("Product Deleted Successfully"));



    }
}
