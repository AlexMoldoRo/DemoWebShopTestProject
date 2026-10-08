package shareddata.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class FirefoxServiceBrowser implements BrowserService {

    private WebDriver driver;

    @Override
    public void openBrowser(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless", "--width=1920", "--height=1080");
        }
        driver = new FirefoxDriver(options);
        if (!headless) {
            driver.manage().window().maximize();
        }
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }
}
