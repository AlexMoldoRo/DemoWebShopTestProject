package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * The green (success) or red (error) bar displayed at the top of the page after
 * "Add to cart" or "Add to wishlist". It is a component, not a page: it can appear on any page.
 */
public class NotificationBar extends BasePage {

    public NotificationBar(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = "#bar-notification .content")
    private WebElement message;

    @FindBy(css = "#bar-notification .close")
    private WebElement closeButton;

    @FindBy(id = "bar-notification")
    private WebElement bar;

    public String getMessage() {
        return helperMethods.getElementText(message);
    }

    public boolean isError() {
        helperMethods.waitElementVisible(bar);
        return bar.getDomAttribute("class").contains("error");
    }

    // The bar covers the header links, so it is closed before clicking on them
    public void close() {
        helperMethods.clickOnElement(closeButton);
        helperMethods.waitElementInvisible(bar);
    }
}
