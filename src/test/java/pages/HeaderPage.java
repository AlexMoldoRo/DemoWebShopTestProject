package pages;

import logger.LoggerUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * The header is displayed on every page of the shop, so it has its own page object.
 */
public class HeaderPage extends BasePage {

    public HeaderPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = ".header-links a.ico-register")
    private WebElement registerLink;

    @FindBy(css = ".header-links a.ico-login")
    private WebElement loginLink;

    @FindBy(css = ".header-links a.ico-logout")
    private WebElement logoutLink;

    // Displayed only for a logged in customer; its text is the customer's email
    @FindBy(css = ".header-links a.account")
    private WebElement accountLink;

    @FindBy(css = ".header-links a.ico-cart")
    private WebElement cartLink;

    @FindBy(css = ".header-links a.ico-wishlist")
    private WebElement wishlistLink;

    // Text like "(3)": the total quantity of products, not the number of lines
    @FindBy(css = ".header-links .cart-qty")
    private WebElement cartQuantity;

    @FindBy(css = ".header-links .wishlist-qty")
    private WebElement wishlistQuantity;

    @FindBy(id = "small-searchterms")
    private WebElement searchBox;

    @FindBy(css = ".search-box input.search-box-button")
    private WebElement searchButton;

    public void clickRegisterLink() {
        helperMethods.clickOnElement(registerLink);
        LoggerUtility.infoLog("Clicked on the Register link");
    }

    public void clickLoginLink() {
        helperMethods.clickOnElement(loginLink);
        LoggerUtility.infoLog("Clicked on the Log in link");
    }

    public void clickLogoutLink() {
        helperMethods.clickOnElement(logoutLink);
        LoggerUtility.infoLog("Clicked on the Log out link");
    }

    public void clickAccountLink() {
        helperMethods.clickOnElement(accountLink);
        LoggerUtility.infoLog("Opened My account");
    }

    public String getLoggedInEmail() {
        return helperMethods.getElementText(accountLink);
    }

    public boolean isLogoutLinkDisplayed() {
        return helperMethods.isElementDisplayed(logoutLink);
    }

    public boolean isLoginLinkDisplayed() {
        return helperMethods.isElementDisplayed(loginLink);
    }

    public void searchFor(String keyword) {
        helperMethods.enterText(searchBox, keyword);
        helperMethods.clickOnElement(searchButton);
        LoggerUtility.infoLog("Searched for '" + keyword + "'");
    }

    public void clickSearchButton() {
        helperMethods.clickOnElement(searchButton);
        LoggerUtility.infoLog("Clicked on the Search button");
    }

    // The shop shows a browser alert when the search box is empty
    public String getSearchAlertTextAndAccept() {
        return helperMethods.getAlertTextAndAccept();
    }

    /**
     * The category is a parameter, so the locator is built here instead of in a @FindBy.
     * XPath reads the text from the HTML ("Books"), not the upper case text shown by the CSS.
     */
    public void clickTopMenuCategory(String categoryName) {
        WebElement category = driver.findElement(
                By.xpath("//ul[@class='top-menu']/li/a[normalize-space()='" + categoryName + "']"));
        helperMethods.clickOnElement(category);
        LoggerUtility.infoLog("Opened the category " + categoryName + " from the top menu");
    }

    public void openShoppingCart() {
        helperMethods.clickOnElement(cartLink);
        LoggerUtility.infoLog("Opened the shopping cart");
    }

    public void openWishlist() {
        helperMethods.clickOnElement(wishlistLink);
        LoggerUtility.infoLog("Opened the wishlist");
    }

    public int getCartQuantity() {
        return extractNumber(helperMethods.getElementText(cartQuantity));
    }

    public int getWishlistQuantity() {
        return extractNumber(helperMethods.getElementText(wishlistQuantity));
    }

    // "(3)" -> 3
    private int extractNumber(String text) {
        return Integer.parseInt(text.replaceAll("[^0-9]", ""));
    }
}
