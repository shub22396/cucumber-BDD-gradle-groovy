package com.lambdatest.stepDefs;

import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.cucumber.java.en.Given;

public class CommonSteps {

    private final StepData stepData;

    public CommonSteps(StepData stepData) {
        this.stepData = stepData;
    }

    @Given("I navigate to website")
    public void iNavigateToWebsite() throws InterruptedException {

        try{
        System.out.println("--------REACHED");
        // stepData.webDriver.get(stepData.url);
        MobileElement color = (MobileElement) stepData.webDriver.findElement(MobileBy.id("com.lambdatest.proverbial:id/color"));
        color.click();
        Thread.sleep(2000);
        MobileElement text = (MobileElement) stepData.webDriver.findElementById("com.lambdatest.proverbial:id/Text");
        //Changes the text to "Proverbial"
        text.click();

        //toast will be visible
        MobileElement toast = (MobileElement) stepData.webDriver.findElementById("com.lambdatest.proverbial:id/toast");
        toast.click();

        //notification will be visible
        MobileElement notification = (MobileElement) stepData.webDriver.findElementById("com.lambdatest.proverbial:id/notification");
        notification.click();
        Thread.sleep(2000);

        //Opens the geolocation page
        MobileElement geo = (MobileElement) stepData.webDriver.findElementById("com.lambdatest.proverbial:id/geoLocation");
        geo.click();
        Thread.sleep(5000);

        //takes back to home page
        MobileElement home = (MobileElement) stepData.webDriver.findElementByAccessibilityId("Home");
        home.click();

        //Takes to speed test page
        MobileElement speedtest = (MobileElement) stepData.webDriver.findElementById("com.lambdatest.proverbial:id/speedTest");
        speedtest.click();
        Thread.sleep(5000);

        MobileElement Home = (MobileElement) stepData.webDriver.findElementByAccessibilityId("Home");
        Home.click();

        //Opens the browser
        MobileElement browser = (MobileElement) stepData.webDriver.findElementByAccessibilityId("Browser");
        browser.click();

        MobileElement url = (MobileElement) stepData.webDriver.findElementById("com.lambdatest.proverbial:id/url");
        url.sendKeys("https://www.lambdatest.com");

        MobileElement find = (MobileElement) stepData.webDriver.findElementById("com.lambdatest.proverbial:id/find");
        find.click();

            stepData.webDriver.executeScript("lambda-status=passed");
    }
        catch (Exception e){
            stepData.webDriver.executeScript("lambda-status=failed");

        }
    }






}
