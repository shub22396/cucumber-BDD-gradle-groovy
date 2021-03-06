package com.lambdatest.stepDefs;

import com.lambdatest.ParallelTest;

import com.lambdatest.utils.Utility;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.FileReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;

public class SetupSteps {

    private final StepData stepData;

    protected String driverBaseLocation = Paths.get(System.getProperty("user.dir"), "/src/test/resources/drivers").toString();

    private static final String PASSED = "passed";
    private static final String FAILED = "failed";
    private static final String URL = "https://ltdemo.com";
    private static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
    private static final String DOCKER_SELENIUM_HUB_URL = "http://localhost:4444/wd/hub";
    private static final String LAMBDATEST_HUB_URL = "https://mobile-hub.lambdatest.com/wd/hub";
    private static final String CAPABILITY_CONFIG_FILE = "src/test/resources/conf/caps.json";
    private static final String REPO_NAME = "-examples-cucumber-junit5 - ";

    public SetupSteps(StepData stepData) {
        this.stepData = stepData;
    }

    @Before
    public void setUp(Scenario scenario) throws Exception {

        JSONParser parser = new JSONParser();
        DesiredCapabilities caps = new DesiredCapabilities();

        String osCaps = Utility.getOsCaps();
        System.out.println("Check environment");
        System.out.println("OSCAPS"+osCaps);
        System.out.println("===o=="+System.getProperty("env"));
        if (StringUtils.isNoneEmpty(System.getProperty("env"))) {
    System.out.println("====="+System.getProperty("env"));
            switch (System.getProperty("env")) {

                case "on-prem":
                    System.out.println("On Prem");
                    break;
                case "docker":
                    System.out.println("Docker");
                    break;
                default:
                    JSONArray environments;
                    JSONObject selectedConfig = new JSONObject();
                    JSONObject testConfig = (JSONObject) parser.parse(new FileReader(CAPABILITY_CONFIG_FILE));

                    if (osCaps.equalsIgnoreCase("parallel")) {

                        System.out.println("//////--"+System.getProperty("parallel"));
                        selectedConfig = ParallelTest.threadLocalValue.get();
                        System.out.println("selectedConfig"+selectedConfig);
                    }
                    else {
                        JSONObject singleCapabilityJson = (JSONObject) ((JSONObject) testConfig.get("tests")).get(osCaps);
                        environments = (JSONArray) singleCapabilityJson.get("env_caps");
                        selectedConfig = Utility.getCombinedCapability((Map<String, String>) environments.get(0), testConfig, singleCapabilityJson);
                    }

                    stepData.url = (String) selectedConfig.get("application_endpoint");
                    String ltUrl = Utility.getRemoteUrl(selectedConfig);
                    Map<String, String> commonCapabilities = (Map<String, String>) selectedConfig.get("capabilities");
                    Iterator it = commonCapabilities.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        if (caps.getCapability(pair.getKey().toString()) == null) {
                            caps.setCapability(pair.getKey().toString(), pair.getValue().toString());
                        }
                    }

                    if (caps.getCapability("lambdatest.local") != null && caps.getCapability("lambdatest.local").equals("true")) {
                        String localIdentifier = RandomStringUtils.randomAlphabetic(8);
                        caps.setCapability("lambdatest.localIdentifier", localIdentifier);
                      //  bstackLocal = new Local();
                        Map<String, String> options = Utility.getLocalOptions(testConfig);
                        System.out.println((String) selectedConfig.get("key"));
                        options.put("key", (String) selectedConfig.get("key"));
                        options.put("localIdentifier", localIdentifier);
                        System.out.println("Local Start");
                        //bstackLocal.start(options);
                    }

                    caps.setCapability("name", scenario.getName());
                    stepData.webDriver = new AppiumDriver(new URL(ltUrl), caps);

            }
        }

        /*
        if (StringUtils.isNoneEmpty(System.getProperty("env")) && System.getProperty("env").equalsIgnoreCase("on-prem")) {

            stepData.url = URL;
            stepData.webDriver = new ChromeDriver();
            stepData.webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
            stepData.webDriver.get(stepData.url);
            stepData.webDriver.manage().window().maximize();

        } else if (StringUtils.isNoneEmpty(System.getProperty("env")) && System.getProperty("env").equalsIgnoreCase("docker")) {

            caps.setBrowserName("chrome");
            caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            stepData.webDriver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), DesiredCapabilities.chrome());
            stepData.webDriver.manage().window().maximize();
            stepData.url = URL;

        } else if (StringUtils.isNoneEmpty(System.getProperty("env")) && System.getProperty("env").equalsIgnoreCase("remote")) {

            JSONArray environments;
            JSONObject capabilityObject;
            JSONObject capabilityJson = (JSONObject) ((JSONObject) config.get("tests")).get(osCaps);
            System.out.println(System.getProperty("parallel"));
            if (System.getProperty("parallel") != null) {
                System.out.println("Check Thread Value");
                System.out.println(ParallelTest.threadLocalValue.get());
                capabilityObject = ParallelTest.threadLocalValue.get();
            } else {
                environments = (JSONArray) capabilityJson.get("env_caps");
                System.out.println("Size is");
                System.out.println(environments.size());
                capabilityObject = Utility.getCombinedCapability((Map<String, String>) environments.get(0), config, capabilityJson);
            }

            stepData.url = (String) capabilityObject.get("application_endpoint");
            String bstackUrl = Utility.getRemoteUrl(capabilityObject);
            Map<String, String> commonCapabilities = (Map<String, String>) capabilityObject.get("capabilities");
            Iterator it = commonCapabilities.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (caps.getCapability(pair.getKey().toString()) == null) {
                    caps.setCapability(pair.getKey().toString(), pair.getValue().toString());
                }
            }

            if (caps.getCapability("LAMBDATEST.local") != null && caps.getCapability("LAMBDATEST.local").equals("true")) {
                String localIdentifier = RandomStringUtils.randomAlphabetic(8);
                caps.setCapability("LAMBDATEST.localIdentifier", localIdentifier);
                bstackLocal = new Local();
                Map<String, String> options = Utility.getLocalOptions(config);
                System.out.println((String) capabilityObject.get("key"));
                options.put("key", (String) capabilityObject.get("key"));
                options.put("localIdentifier", localIdentifier);
                System.out.println("Local Start");
                bstackLocal.start(options);
            }

            caps.setCapability("name", scenario.getName());
            stepData.webDriver = new RemoteWebDriver(new URL(bstackUrl), caps);
        }
        */
    }

    private void setupLocal(DesiredCapabilities caps, JSONObject testConfigs, String accessKey) throws Exception {
        if (caps.getCapability("lambdatest.local") != null && caps.getCapability("lambdatest.local").equals("true")) {
            String localIdentifier = RandomStringUtils.randomAlphabetic(8);
            caps.setCapability("lambdatest.localIdentifier", localIdentifier);
            //bstackLocal = new Local();
            Map<String, String> options = Utility.getLocalOptions(testConfigs);
            options.put("key", accessKey);
            options.put("localIdentifier", localIdentifier);
            //bstackLocal.start(options);
        }
    }

    @After
    public void teardown(Scenario scenario) throws Exception {
        stepData.webDriver.quit();
    }
}
