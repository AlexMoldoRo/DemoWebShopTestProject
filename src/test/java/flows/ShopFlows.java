package flows;

import helpermethods.TestDataGenerator;
import objectdata.RegisterObject;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.CartPage;
import pages.CategoryPage;
import pages.CheckoutAsGuestPage;
import pages.CheckoutPage;
import pages.HeaderPage;
import pages.NotificationBar;
import pages.ProductPage;
import pages.RegisterPage;
import pages.WishlistPage;

/**
 * Sequences of steps that many tests need as a precondition. They are written once, here,
 * instead of being copied in every test class. Page objects describe one page;
 * a flow combines several pages into a user journey.
 */
public class ShopFlows {

    private final WebDriver driver;

    public ShopFlows(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Registers a new customer with a generated email. Registration logs the customer in.
     */
    public RegisterObject registerNewUser(RegisterObject user) {
        user.setEmail(TestDataGenerator.uniqueEmail());
        HeaderPage headerPage = new HeaderPage(driver);
        headerPage.clickRegisterLink();
        new RegisterPage(driver).registerUser(user);
        Assert.assertEquals(headerPage.getLoggedInEmail(), user.getEmail(), "Precondition failed: user not registered");
        return user;
    }

    // A registered customer who is logged out again (ready to test the login)
    public RegisterObject registerNewUserAndLogOut(RegisterObject user) {
        registerNewUser(user);
        new HeaderPage(driver).clickLogoutLink();
        return user;
    }

    public ProductPage openProductPage(String category, String productName) {
        new HeaderPage(driver).clickTopMenuCategory(category);
        new CategoryPage(driver).openProduct(productName);
        return new ProductPage(driver);
    }

    // Precondition: the cart contains one product
    public CartPage addProductToCartAndOpenCart(String category, String productName) {
        openProductPage(category, productName).clickAddToCart();
        closeNotification();
        new HeaderPage(driver).openShoppingCart();
        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.containsProduct(productName), "Precondition failed: the product is not in the cart");
        return cartPage;
    }

    // Precondition: the wishlist contains one product
    public WishlistPage addProductToWishlistAndOpenWishlist(String category, String productName) {
        openProductPage(category, productName).clickAddToWishlist();
        closeNotification();
        new HeaderPage(driver).openWishlist();
        WishlistPage wishlistPage = new WishlistPage(driver);
        Assert.assertTrue(wishlistPage.containsProduct(productName), "Precondition failed: the product is not in the wishlist");
        return wishlistPage;
    }

    /**
     * Cart with one product -> terms of service accepted -> Checkout -> Checkout as Guest.
     * Returns the checkout page, open at the billing address step.
     */
    public CheckoutPage startGuestCheckout(String category, String productName) {
        CartPage cartPage = addProductToCartAndOpenCart(category, productName);
        cartPage.acceptTermsOfService();
        cartPage.clickCheckout();
        new CheckoutAsGuestPage(driver).clickCheckoutAsGuest();
        return new CheckoutPage(driver);
    }

    // Waits for the notification of the last action, then closes it (it covers the header links)
    private void closeNotification() {
        NotificationBar notificationBar = new NotificationBar(driver);
        notificationBar.getMessage();
        notificationBar.close();
    }
}
