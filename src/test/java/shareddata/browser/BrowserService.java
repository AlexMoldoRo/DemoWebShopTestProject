package shareddata.browser;

import org.openqa.selenium.WebDriver;

/**
 * Contract every supported browser has to respect.
 */
public interface BrowserService {

    void openBrowser(boolean headless);

    WebDriver getDriver();
}
