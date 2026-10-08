package shareddata.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class EdgeServiceBrowser implements BrowserService {

    private WebDriver driver;

    @Override
    public void openBrowser(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        driver = new EdgeDriver(options);
    }

    @Override
    public WebDriver getDriver() {
        return driver;
    }
}
