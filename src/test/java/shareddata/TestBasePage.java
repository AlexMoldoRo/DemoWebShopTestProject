package shareddata;

import com.aventstack.chaintest.plugins.ChainTestListener;
import configutility.ConfigurationReader;
import logger.LoggerUtility;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import shareddata.browser.BrowserFactory;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Parent of all test classes: opens a new browser before each test and closes it after,
 * so the tests do not depend on each other.
 */
@Listeners(ChainTestListener.class)
public class TestBasePage {

    private WebDriver driver;
    private String testName;

    @BeforeMethod(alwaysRun = true)
    public void initialiseBrowser(Method method) {
        testName = this.getClass().getSimpleName() + "." + method.getName();
        LoggerUtility.startTestCase(testName);
        driver = new BrowserFactory().getBrowserDriver();
        LoggerUtility.infoLog("The browser started successfully");
        driver.get(ConfigurationReader.getConfiguration().getBaseUrl());
    }

    @AfterMethod(alwaysRun = true)
    public void clearBrowser(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                LoggerUtility.errorLog(String.valueOf(result.getThrowable()));
                takeScreenshot();
            }
        } finally {
            // The browser is closed even if the screenshot fails
            if (driver != null) {
                driver.quit();
                LoggerUtility.infoLog("The browser was closed successfully");
            }
            LoggerUtility.endTestCase(testName);
        }
    }

    @AfterSuite(alwaysRun = true)
    public void finishLogFiles() {
        LoggerUtility.mergeFiles();
    }

    // Saves the screenshot in target/screenshots and attaches it to the HTML report
    private void takeScreenshot() {
        if (driver == null) {
            return;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path folder = Paths.get("target", "screenshots");
            Files.createDirectories(folder);
            Files.write(folder.resolve(testName + ".png"), screenshot);
            ChainTestListener.embed(screenshot, "image/png");
            LoggerUtility.infoLog("Screenshot saved for the failed test");
        } catch (Exception e) {
            LoggerUtility.errorLog("Could not take the screenshot: " + e.getMessage());
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
