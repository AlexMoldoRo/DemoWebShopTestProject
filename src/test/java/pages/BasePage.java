package pages;

import helpermethods.HelperMethods;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

/**
 * Parent of all page objects: what every page needs is written once, here.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected HelperMethods helperMethods;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.helperMethods = new HelperMethods(driver);
        // Without this line the @FindBy elements of the page are never looked up
        PageFactory.initElements(driver, this);
    }
}
