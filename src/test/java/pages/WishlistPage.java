package pages;

import logger.LoggerUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class WishlistPage extends BasePage {

    public WishlistPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = "input.update-wishlist-button")
    private WebElement updateWishlistButton;

    @FindBy(css = "input.wishlist-add-to-cart-button")
    private WebElement addToCartButton;

    @FindBy(css = ".wishlist-content")
    private WebElement wishlistContent;

    public boolean containsProduct(String productName) {
        return !driver.findElements(By.xpath(productRow(productName))).isEmpty();
    }

    public void moveProductToCart(String productName) {
        helperMethods.clickOnElement(cellOf(productName, "input[@name='addtocart']"));
        helperMethods.clickOnElement(addToCartButton);
        LoggerUtility.infoLog("Moved " + productName + " from the wishlist to the cart");
    }

    public void removeProduct(String productName) {
        helperMethods.clickOnElement(cellOf(productName, "input[@name='removefromcart']"));
        helperMethods.clickOnElement(updateWishlistButton);
        LoggerUtility.infoLog("Removed " + productName + " from the wishlist");
    }

    // "The wishlist is empty!" when there is no product
    public String getEmptyWishlistMessage() {
        return helperMethods.getElementText(wishlistContent);
    }

    private String productRow(String productName) {
        return "//tr[@class='cart-item-row'][.//td[@class='product']/a[normalize-space()='" + productName + "']]";
    }

    private WebElement cellOf(String productName, String elementXpath) {
        return driver.findElement(By.xpath(productRow(productName) + "//" + elementXpath));
    }
}
