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
 *
 * The driver is kept in a ThreadLocal: every thread that runs tests gets its own browser,
 * even when TestNG runs several test methods of the same class (same object) in parallel.
 */
@Listeners(ChainTestListener.class)
public class TestBasePage {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<String> testName = new ThreadLocal<>();

    @BeforeMethod(alwaysRun = true)
    public void initialiseBrowser(Method method) {
        testName.set(this.getClass().getSimpleName() + "." + method.getName());
        LoggerUtility.startTestCase(testName.get());
        driver.set(new BrowserFactory().getBrowserDriver());
        LoggerUtility.infoLog("The browser started successfully");
        getDriver().get(ConfigurationReader.getConfiguration().getBaseUrl());
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
            if (getDriver() != null) {
                getDriver().quit();
                LoggerUtility.infoLog("The browser was closed successfully");
            }
            LoggerUtility.endTestCase(testName.get());
            // Threads are reused by TestNG: the values of this test must not stay behind
            driver.remove();
            testName.remove();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void finishLogFiles() {
        LoggerUtility.mergeFiles();
    }

    // Saves the screenshot in target/screenshots and attaches it to the HTML report
    private void takeScreenshot() {
        if (getDriver() == null) {
            return;
        }
        try {
            byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
            Path folder = Paths.get("target", "screenshots");
            Files.createDirectories(folder);
            Files.write(folder.resolve(testName.get() + ".png"), screenshot);
            ChainTestListener.embed(screenshot, "image/png");
            LoggerUtility.infoLog("Screenshot saved for the failed test");
        } catch (Exception e) {
            LoggerUtility.errorLog("Could not take the screenshot: " + e.getMessage());
        }
    }

    // The browser of the current thread
    public WebDriver getDriver() {
        return driver.get();
    }
}
