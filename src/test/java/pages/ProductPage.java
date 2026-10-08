package pages;

import logger.LoggerUtility;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * The details page of one product.
 */
public class ProductPage extends BasePage {

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = ".product-essential .product-name h1")
    private WebElement productName;

    @FindBy(css = ".product-essential .product-price span")
    private WebElement productPrice;

    @FindBy(css = ".product-essential input.qty-input")
    private WebElement quantityField;

    // The id ends with the id of the product (add-to-cart-button-22), so only its start is used
    @FindBy(css = ".product-essential input[id^='add-to-cart-button']")
    private WebElement addToCartButton;

    @FindBy(css = ".product-essential input[id^='add-to-wishlist-button']")
    private WebElement addToWishlistButton;

    public String getProductName() {
        return helperMethods.getElementText(productName);
    }

    public double getProductPrice() {
        return helperMethods.getPrice(productPrice);
    }

    public void enterQuantity(String quantity) {
        helperMethods.enterText(quantityField, quantity);
    }

    // The product is added without reloading the page; the result is shown in the notification bar
    public void clickAddToCart() {
        helperMethods.clickOnElement(addToCartButton);
        LoggerUtility.infoLog("Clicked on Add to cart");
    }

    public void clickAddToWishlist() {
        helperMethods.clickOnElement(addToWishlistButton);
        LoggerUtility.infoLog("Clicked on Add to wishlist");
    }

    public boolean isAddToCartButtonDisplayed() {
        return helperMethods.isElementDisplayed(addToCartButton);
    }

    public boolean isAddToWishlistButtonDisplayed() {
        return helperMethods.isElementDisplayed(addToWishlistButton);
    }
}
