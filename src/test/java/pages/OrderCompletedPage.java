package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * "Thank you" page displayed after the order is confirmed.
 */
public class OrderCompletedPage extends BasePage {

    public OrderCompletedPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = ".order-completed .title strong")
    private WebElement successMessage;

    // Text like "Order number: 2396389"
    @FindBy(xpath = "//div[contains(@class,'order-completed')]//ul[@class='details']/li[contains(normalize-space(),'Order number')]")
    private WebElement orderNumber;

    public String getSuccessMessage() {
        return helperMethods.getElementText(successMessage);
    }

    public String getOrderNumber() {
        return helperMethods.getElementText(orderNumber).replaceAll("[^0-9]", "");
    }
}
