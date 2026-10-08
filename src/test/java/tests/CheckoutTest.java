package tests;

import helpermethods.TestDataGenerator;
import objectdata.CheckoutObject;
import objectdata.RegisterObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CategoryPage;
import pages.CheckoutAsGuestPage;
import pages.CheckoutPage;
import pages.HeaderPage;
import pages.MyAccountPage;
import pages.NotificationBar;
import pages.OrderCompletedPage;
import pages.ProductPage;
import pages.RegisterPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.Map;

/**
 * These tests place real orders on the demo shop (paid "Cash On Delivery", with invented data).
 */
public class CheckoutTest extends TestBasePage {

    private Map<String, CheckoutObject> checkoutData;
    private Map<String, RegisterObject> registerData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {
        checkoutData = XmlDataLoader.loadData("CheckoutData.xml", CheckoutObject.class);
        registerData = XmlDataLoader.loadData("RegisterData.xml", RegisterObject.class);
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_01 - Checkout without accepting the terms of service")
    public void checkoutWithoutAcceptingTerms() {
        CheckoutObject data = checkoutData.get("termsNotAccepted");
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());

        cartPage.clickCheckout();

        Assert.assertEquals(cartPage.getTermsOfServiceWarning(), data.getExpectedMessage());
        Assert.assertTrue(getDriver().getCurrentUrl().endsWith("/cart"), "The checkout started");
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_CHK_02 - Place an order as a guest")
    public void placeOrderAsGuest() {
        CheckoutObject data = guestData();
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());

        cartPage.acceptTermsOfService();
        cartPage.clickCheckout();
        new CheckoutAsGuestPage(getDriver()).clickCheckoutAsGuest();

        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.goToConfirmOrderStep(data);
        checkoutPage.confirmOrder();

        OrderCompletedPage orderCompletedPage = new OrderCompletedPage(getDriver());
        Assert.assertEquals(orderCompletedPage.getSuccessMessage(), data.getExpectedMessage());
        Assert.assertFalse(orderCompletedPage.getOrderNumber().isEmpty(), "No order number is displayed");
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_03 - Place an order as a registered customer")
    public void placeOrderAsRegisteredCustomer() {
        // Precondition: a new customer is registered and logged in
        RegisterObject user = registerData.get("validUser");
        user.setEmail(TestDataGenerator.uniqueEmail());
        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickRegisterLink();
        new RegisterPage(getDriver()).registerUser(user);
        Assert.assertEquals(headerPage.getLoggedInEmail(), user.getEmail(), "Precondition failed: user not registered");

        CheckoutObject data = checkoutData.get("guestOrder");
        data.setEmail(user.getEmail());
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());

        // A logged in customer goes straight to the checkout, without the guest page
        cartPage.acceptTermsOfService();
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.goToConfirmOrderStep(data);
        checkoutPage.confirmOrder();

        OrderCompletedPage orderCompletedPage = new OrderCompletedPage(getDriver());
        Assert.assertEquals(orderCompletedPage.getSuccessMessage(), data.getExpectedMessage());
        String orderNumber = orderCompletedPage.getOrderNumber();

        headerPage.clickAccountLink();
        MyAccountPage myAccountPage = new MyAccountPage(getDriver());
        myAccountPage.openOrders();
        Assert.assertTrue(myAccountPage.getOrderListText().contains(orderNumber),
                "Order " + orderNumber + " is not in the customer's orders");
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_04 - Billing address with mandatory fields empty")
    public void billingAddressWithEmptyMandatoryFields() {
        CheckoutObject data = checkoutData.get("guestOrder");
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());
        cartPage.acceptTermsOfService();
        cartPage.clickCheckout();
        new CheckoutAsGuestPage(getDriver()).clickCheckoutAsGuest();

        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.continueFromBillingAddress();

        Assert.assertEquals(checkoutPage.getBillingFieldError("FirstName"), "First name is required.");
        Assert.assertEquals(checkoutPage.getBillingFieldError("LastName"), "Last name is required.");
        Assert.assertEquals(checkoutPage.getBillingFieldError("Email"), "Email is required.");
        Assert.assertEquals(checkoutPage.getBillingFieldError("CountryId"), "Country is required.");
        Assert.assertEquals(checkoutPage.getBillingFieldError("City"), "City is required");
        Assert.assertEquals(checkoutPage.getBillingFieldError("Address1"), "Street address is required");
        Assert.assertEquals(checkoutPage.getBillingFieldError("ZipPostalCode"), "Zip / postal code is required");
        Assert.assertEquals(checkoutPage.getBillingFieldError("PhoneNumber"), "Phone is required");
        Assert.assertTrue(checkoutPage.isBillingStepOpen(), "The checkout left the billing address step");
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_05 - Order total on the confirmation step")
    public void orderTotalOnConfirmationStep() {
        CheckoutObject data = guestData();
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());
        double cartSubTotal = cartPage.getSubTotal();

        cartPage.acceptTermsOfService();
        cartPage.clickCheckout();
        new CheckoutAsGuestPage(getDriver()).clickCheckoutAsGuest();
        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.goToConfirmOrderStep(data);

        double subTotal = checkoutPage.getConfirmTotal("Sub-Total:");
        double shipping = checkoutPage.getConfirmTotal("Shipping:");
        double paymentFee = checkoutPage.getConfirmTotal("Payment method additional fee:");
        double tax = checkoutPage.getConfirmTotal("Tax:");
        double total = checkoutPage.getConfirmTotal("Total:");

        Assert.assertEquals(subTotal, cartSubTotal, "Sub-Total differs from the cart");
        // Rounded to cents, so a difference like 0.0000001 between doubles does not fail the test
        Assert.assertEquals(Math.round(total * 100), Math.round((subTotal + shipping + paymentFee + tax) * 100),
                "Total is not Sub-Total + Shipping + payment fee + Tax");
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_06 - Cart is emptied after the order is placed")
    public void cartIsEmptyAfterOrder() {
        CheckoutObject data = guestData();
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());
        cartPage.acceptTermsOfService();
        cartPage.clickCheckout();
        new CheckoutAsGuestPage(getDriver()).clickCheckoutAsGuest();
        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.goToConfirmOrderStep(data);
        checkoutPage.confirmOrder();
        new OrderCompletedPage(getDriver()).getSuccessMessage();

        HeaderPage headerPage = new HeaderPage(getDriver());
        Assert.assertEquals(headerPage.getCartQuantity(), 0, "Wrong number in the header");
        headerPage.openShoppingCart();
        Assert.assertEquals(new CartPage(getDriver()).getEmptyCartMessage(),
                checkoutData.get("emptyCartMessage").getExpectedMessage());
    }

    private CheckoutObject guestData() {
        CheckoutObject data = checkoutData.get("guestOrder");
        data.setEmail(TestDataGenerator.uniqueEmail());
        return data;
    }

    // Precondition shared by all tests: the cart contains one product
    private CartPage addProductAndOpenCart(String category, String productName) {
        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(category);
        new CategoryPage(getDriver()).openProduct(productName);
        new ProductPage(getDriver()).clickAddToCart();
        NotificationBar notificationBar = new NotificationBar(getDriver());
        notificationBar.getMessage();
        notificationBar.close();
        headerPage.openShoppingCart();
        CartPage cartPage = new CartPage(getDriver());
        Assert.assertTrue(cartPage.containsProduct(productName), "Precondition failed: the product is not in the cart");
        return cartPage;
    }
}
