package pages;

import logger.LoggerUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CartPage extends BasePage {

    public CartPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = ".cart-item-row .product a.product-name")
    private List<WebElement> productNames;

    @FindBy(css = "input.update-cart-button")
    private WebElement updateCartButton;

    @FindBy(id = "termsofservice")
    private WebElement termsOfServiceCheckbox;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    // Pop-up displayed when Checkout is clicked without accepting the terms of service
    @FindBy(id = "terms-of-service-warning-box")
    private WebElement termsOfServiceWarning;

    @FindBy(css = ".order-summary-content")
    private WebElement summaryContent;

    @FindBy(xpath = "//table[@class='cart-total']//tr[td/span[contains(normalize-space(),'Sub-Total')]]//span[@class='product-price']")
    private WebElement subTotal;

    public List<String> getProductNames() {
        return helperMethods.getElementsTexts(productNames);
    }

    public boolean containsProduct(String productName) {
        return !driver.findElements(By.xpath(productRow(productName))).isEmpty();
    }

    public double getUnitPrice(String productName) {
        return helperMethods.getPrice(cellOf(productName, "span[@class='product-unit-price']"));
    }

    public int getQuantity(String productName) {
        WebElement quantity = cellOf(productName, "input[contains(@class,'qty-input')]");
        return Integer.parseInt(quantity.getDomProperty("value"));
    }

    public double getLineTotal(String productName) {
        return helperMethods.getPrice(cellOf(productName, "span[@class='product-subtotal']"));
    }

    public double getSubTotal() {
        return helperMethods.getPrice(subTotal);
    }

    public void updateQuantity(String productName, String quantity) {
        helperMethods.enterText(cellOf(productName, "input[contains(@class,'qty-input')]"), quantity);
        helperMethods.clickAndWaitForPageReload(updateCartButton);
        LoggerUtility.infoLog("Changed the quantity of " + productName + " to " + quantity);
    }

    public void removeProduct(String productName) {
        helperMethods.clickOnElement(cellOf(productName, "input[@name='removefromcart']"));
        helperMethods.clickAndWaitForPageReload(updateCartButton);
        LoggerUtility.infoLog("Removed " + productName + " from the cart");
    }

    public void acceptTermsOfService() {
        if (!termsOfServiceCheckbox.isSelected()) {
            helperMethods.clickOnElement(termsOfServiceCheckbox);
        }
    }

    public void clickCheckout() {
        helperMethods.clickOnElement(checkoutButton);
        LoggerUtility.infoLog("Clicked on Checkout");
    }

    public String getTermsOfServiceWarning() {
        return helperMethods.getElementText(termsOfServiceWarning);
    }

    // "Your Shopping Cart is empty!" when there is no product
    public String getEmptyCartMessage() {
        return helperMethods.getElementText(summaryContent);
    }

    // The row (tr) of the table that contains the product
    private String productRow(String productName) {
        return "//tr[@class='cart-item-row'][.//td[@class='product']/a[normalize-space()='" + productName + "']]";
    }

    private WebElement cellOf(String productName, String elementXpath) {
        return driver.findElement(By.xpath(productRow(productName) + "//" + elementXpath));
    }
}
