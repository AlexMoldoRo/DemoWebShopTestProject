package shareddata.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeServiceBrowser implements BrowserService {

    private WebDriver driver;

    @Override
    public void openBrowser(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        if (headless) {
            // In headless mode there is no window to maximize, so the size is set explicitly
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }
}
