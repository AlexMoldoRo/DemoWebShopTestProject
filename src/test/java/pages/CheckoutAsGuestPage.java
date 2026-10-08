package pages;

import logger.LoggerUtility;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Displayed after Checkout when no customer is logged in: checkout as a guest, register or log in.
 */
public class CheckoutAsGuestPage extends BasePage {

    public CheckoutAsGuestPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = "input.checkout-as-guest-button")
    private WebElement checkoutAsGuestButton;

    public void clickCheckoutAsGuest() {
        helperMethods.clickOnElement(checkoutAsGuestButton);
        LoggerUtility.infoLog("Clicked on Checkout as Guest");
    }
}
