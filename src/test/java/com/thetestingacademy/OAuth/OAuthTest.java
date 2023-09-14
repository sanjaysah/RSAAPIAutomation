package com.thetestingacademy.OAuth;

import com.thetestingacademy.PojoClasses.Api;
import com.thetestingacademy.PojoClasses.Oauth2Response;
import com.thetestingacademy.PojoClasses.WebAutomation;
import io.restassured.RestAssured;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OAuthTest {

    //First using Selenium and new Account without 2 way authentication/recovery setup
    // and then Restassured codes API

    public static void main(String[] args) throws InterruptedException {
        String[] expected = {"Selenium Webdriver Java","Cypress","Protractor"};
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        //GeneralClass te =  new GeneralClass ();
        driver.get("https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/userinfo.email&auth_url=https://accounts.google.com/o/oauth2/v2/auth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https://rahulshettyacademy.com/getCourse.php&state=verifyfjdss");
        driver.findElement(By.xpath("//input[@type='email']")).sendKeys("automation260923");
        driver.findElement(By.xpath("//span[text()='Next']")).click();
        Thread.sleep(4000);
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys("automation123");
        driver.findElement(By.xpath("//input[@type='password']")).sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        String oathURL = driver.getCurrentUrl();
        String first = oathURL.split("&code=")[1];
        String code = first.split("&scope")[0];
        String responseAccessKey = given().queryParam("code",code)
                .urlEncodingEnabled(false)
                .queryParam("client_id","692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .queryParam("client_secret","erZOWM9g3UtwNRj340YYaK_W")
                .queryParam("redirect_uri","https://rahulshettyacademy.com/getCourse.php")
                .queryParam("grant_type","authorization_code")
                .when().log().all().post("https://www.googleapis.com/oauth2/v4/token").asString();
        JsonPath path = new JsonPath(responseAccessKey);
        String access_token = path.getString("access_token");

        Oauth2Response response = given().queryParam("access_token",access_token)
                .expect().defaultParser(Parser.JSON)
                .when().get("https://rahulshettyacademy.com/getCourse.php")
                .as(Oauth2Response.class);
        Thread.sleep(4000);
//        System.out.println(response.getLinkedIn());
//        System.out.println(response.getInstructor());

        List<Api> apiList = response.getCourses().getApi();
        for (Api a:apiList){
            if ((a.courseTitle.equals("SoapUI Webservices testing"))) {
                System.out.println(a.price);
                break;
            }
        }
        ArrayList arr = new ArrayList<>();
        List<WebAutomation> WAList = response.getCourses().getWebAutomation();
        for (WebAutomation wa:WAList) {
            arr.add(wa.courseTitle);
        }
        Assert.assertTrue(arr.equals(Arrays.asList(expected)));
        //driver.quit();
    }
}
