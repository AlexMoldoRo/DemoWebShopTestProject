package pages;

import logger.LoggerUtility;
import objectdata.CheckoutObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * The one page checkout. The six steps are sections of the same page: each "Continue"
 * saves its step in the background (AJAX) and opens the next section without reloading the page.
 */
public class CheckoutPage extends BasePage {

    /*
     * The demo shop sometimes needs more than 10 seconds to save a checkout step,
     * so the checkout waits longer than the rest of the framework.
     */
    private static final int STEP_TIMEOUT_SECONDS = 30;

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // Step 1 - Billing address
    @FindBy(id = "BillingNewAddress_FirstName")
    private WebElement firstNameField;

    @FindBy(id = "BillingNewAddress_LastName")
    private WebElement lastNameField;

    @FindBy(id = "BillingNewAddress_Email")
    private WebElement emailField;

    @FindBy(id = "BillingNewAddress_CountryId")
    private WebElement countryDropdown;

    @FindBy(id = "BillingNewAddress_City")
    private WebElement cityField;

    @FindBy(id = "BillingNewAddress_Address1")
    private WebElement addressField;

    @FindBy(id = "BillingNewAddress_ZipPostalCode")
    private WebElement zipCodeField;

    @FindBy(id = "BillingNewAddress_PhoneNumber")
    private WebElement phoneField;

    // Every step has its own Continue button; only the one of the open step is visible
    @FindBy(css = "#opc-billing input.new-address-next-step-button")
    private WebElement billingContinueButton;

    // Step 2 - Shipping address (the billing address is selected by default)
    @FindBy(css = "#opc-shipping input.new-address-next-step-button")
    private WebElement shippingAddressContinueButton;

    // Step 3 - Shipping method (Ground is selected by default)
    @FindBy(css = "#opc-shipping_method input.shipping-method-next-step-button")
    private WebElement shippingMethodContinueButton;

    // Step 4 - Payment method (Cash On Delivery is selected by default)
    @FindBy(css = "#opc-payment_method input.payment-method-next-step-button")
    private WebElement paymentMethodContinueButton;

    // Step 5 - Payment information
    @FindBy(css = "#opc-payment_info input.payment-info-next-step-button")
    private WebElement paymentInfoContinueButton;

    // Step 6 - Confirm order
    @FindBy(css = "#opc-confirm_order input.confirm-order-next-step-button")
    private WebElement confirmButton;

    public void fillBillingAddress(CheckoutObject data) {
        helperMethods.enterText(firstNameField, data.getFirstName());
        helperMethods.enterText(lastNameField, data.getLastName());
        helperMethods.enterText(emailField, data.getEmail());
        helperMethods.selectByText(countryDropdown, data.getCountry());
        helperMethods.enterText(cityField, data.getCity());
        helperMethods.enterText(addressField, data.getAddress());
        helperMethods.enterText(zipCodeField, data.getZipCode());
        helperMethods.enterText(phoneField, data.getPhone());
        LoggerUtility.infoLog("Filled in the billing address");
    }

    public void continueFromBillingAddress() {
        clickContinue(billingContinueButton);
    }

    /**
     * Steps 2 to 5 with the default choices: same address for shipping, Ground, Cash On Delivery.
     */
    public void continueWithDefaultOptions() {
        clickContinue(shippingAddressContinueButton);
        clickContinue(shippingMethodContinueButton);
        clickContinue(paymentMethodContinueButton);
        clickContinue(paymentInfoContinueButton);
        LoggerUtility.infoLog("Continued with the default shipping and payment options");
    }

    // From the billing address to the Confirm order step
    public void goToConfirmOrderStep(CheckoutObject data) {
        fillBillingAddress(data);
        continueFromBillingAddress();
        continueWithDefaultOptions();
    }

    public void confirmOrder() {
        helperMethods.clickOnElement(confirmButton);
        LoggerUtility.infoLog("Clicked on Confirm");
        helperMethods.waitUrlContains("/checkout/completed", STEP_TIMEOUT_SECONDS);
    }

    /**
     * Clicks the Continue button of a step and waits until the shop has finished saving it.
     * While a step is saved, the shop's own script keeps Checkout.loadWaiting different from false;
     * when the answer arrives, it redraws the section (the old elements become "stale") and then
     * sets Checkout.loadWaiting back to false. Only after that the next section can be used safely.
     */
    private void clickContinue(WebElement continueButton) {
        helperMethods.clickOnElement(continueButton);
        helperMethods.waitJavascriptCondition("Checkout.loadWaiting === false", STEP_TIMEOUT_SECONDS);
    }

    /**
     * Validation message of a billing field, for example "FirstName" -> "First name is required."
     */
    public String getBillingFieldError(String fieldName) {
        WebElement error = driver.findElement(
                By.cssSelector("#opc-billing span[data-valmsg-for='BillingNewAddress." + fieldName + "']"));
        return helperMethods.getElementText(error);
    }

    public boolean isBillingStepOpen() {
        return helperMethods.isElementDisplayed(billingContinueButton);
    }

    /**
     * An amount from the totals table of the Confirm order step.
     * Labels: "Sub-Total:", "Shipping:", "Payment method additional fee:", "Tax:", "Total:"
     */
    public double getConfirmTotal(String label) {
        WebElement amount = driver.findElement(By.xpath("//li[@id='opc-confirm_order']//table[@class='cart-total']"
                + "//tr[td[@class='cart-total-left']/span[@class='nobr' and normalize-space()='" + label + "']]"
                + "//span[contains(@class,'product-price')]"));
        return helperMethods.getPrice(amount);
    }
}
