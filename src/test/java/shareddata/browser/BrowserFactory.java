package shareddata.browser;

import configutility.Configuration;
import configutility.ConfigurationReader;
import org.openqa.selenium.WebDriver;

/**
 * Factory pattern: the tests ask for "a browser" and this class decides which one to start,
 * based on GeneralConfiguration.xml (or on -Dbrowser / -Dheadless).
 */
public class BrowserFactory {

    public WebDriver getBrowserDriver() {
        Configuration configuration = ConfigurationReader.getConfiguration();
        String browser = configuration.getBrowser();

        BrowserService browserService;
        switch (browser) {
            case BrowserType.BROWSER_CHROME:
                browserService = new ChromeServiceBrowser();
                break;
            case BrowserType.BROWSER_EDGE:
                browserService = new EdgeServiceBrowser();
                break;
            case BrowserType.BROWSER_FIREFOX:
                browserService = new FirefoxServiceBrowser();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: '" + browser
                        + "'. Supported values: chrome, edge, firefox");
        }

        browserService.openBrowser(configuration.isHeadless());
        return browserService.getDriver();
    }
}
