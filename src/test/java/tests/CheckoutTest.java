package tests;

import flows.ShopFlows;
import helpermethods.TestDataGenerator;
import objectdata.CheckoutObject;
import objectdata.RegisterObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.CartPage;
import pages.CheckoutAsGuestPage;
import pages.CheckoutPage;
import pages.HeaderPage;
import pages.MyAccountPage;
import pages.OrderCompletedPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.Map;

/**
 * These tests place real orders on the demo shop (paid "Cash On Delivery", with invented data).
 */
public class CheckoutTest extends TestBasePage {

    private static final String DATA_FILE = "CheckoutData.xml";

    @Test(groups = {"regression"},
            description = "TC_CHK_01 - Checkout without accepting the terms of service")
    public void checkoutWithoutAcceptingTerms() {
        CheckoutObject data = XmlDataLoader.loadDataSet(DATA_FILE, "termsNotAccepted", CheckoutObject.class);
        CartPage cartPage = new ShopFlows(getDriver()).addProductToCartAndOpenCart(data.getCategory(), data.getProductName());

        cartPage.clickCheckout();

        Assert.assertEquals(cartPage.getTermsOfServiceWarning(), data.getExpectedMessage());
        Assert.assertTrue(getDriver().getCurrentUrl().endsWith("/cart"), "The checkout started");
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_CHK_02 - Place an order as a guest")
    public void placeOrderAsGuest() {
        CheckoutObject data = XmlDataLoader.loadDataSet(DATA_FILE, "guestOrder", CheckoutObject.class);
        data.setEmail(TestDataGenerator.uniqueEmail());
        CheckoutPage checkoutPage = new ShopFlows(getDriver()).startGuestCheckout(data.getCategory(), data.getProductName());

        checkoutPage.goToConfirmOrderStep(data);
        checkoutPage.confirmOrder();

        OrderCompletedPage orderCompletedPage = new OrderCompletedPage(getDriver());
        Assert.assertEquals(orderCompletedPage.getSuccessMessage(), data.getExpectedMessage());
        Assert.assertFalse(orderCompletedPage.getOrderNumber().isEmpty(), "No order number is displayed");
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_03 - Place an order as a registered customer")
    public void placeOrderAsRegisteredCustomer() {
        ShopFlows shopFlows = new ShopFlows(getDriver());

        // Precondition: a new customer is registered and logged in
        RegisterObject user = shopFlows.registerNewUser(
                XmlDataLoader.loadDataSet("RegisterData.xml", "validUser", RegisterObject.class));

        CheckoutObject data = XmlDataLoader.loadDataSet(DATA_FILE, "guestOrder", CheckoutObject.class);
        data.setEmail(user.getEmail());
        CartPage cartPage = shopFlows.addProductToCartAndOpenCart(data.getCategory(), data.getProductName());

        // A logged in customer goes straight to the checkout, without the guest page
        cartPage.acceptTermsOfService();
        cartPage.clickCheckout();
        CheckoutPage checkoutPage = new CheckoutPage(getDriver());
        checkoutPage.goToConfirmOrderStep(data);
        checkoutPage.confirmOrder();

        OrderCompletedPage orderCompletedPage = new OrderCompletedPage(getDriver());
        Assert.assertEquals(orderCompletedPage.getSuccessMessage(), data.getExpectedMessage());
        String orderNumber = orderCompletedPage.getOrderNumber();

        new HeaderPage(getDriver()).clickAccountLink();
        MyAccountPage myAccountPage = new MyAccountPage(getDriver());
        myAccountPage.openOrders();
        Assert.assertTrue(myAccountPage.getOrderListText().contains(orderNumber),
                "Order " + orderNumber + " is not in the customer's orders");
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_04 - Billing address with mandatory fields empty")
    public void billingAddressWithEmptyMandatoryFields() {
        CheckoutObject data = XmlDataLoader.loadDataSet(DATA_FILE, "emptyBillingAddress", CheckoutObject.class);
        CheckoutPage checkoutPage = new ShopFlows(getDriver()).startGuestCheckout(data.getCategory(), data.getProductName());

        checkoutPage.continueFromBillingAddress();

        // SoftAssert checks every field and reports all wrong messages, not only the first one
        SoftAssert softAssert = new SoftAssert();
        for (Map.Entry<String, String> expected : data.getExpectedErrors().entrySet()) {
            softAssert.assertEquals(checkoutPage.getBillingFieldError(expected.getKey()), expected.getValue(),
                    "Wrong message for " + expected.getKey());
        }
        softAssert.assertTrue(checkoutPage.isBillingStepOpen(), "The checkout left the billing address step");
        softAssert.assertAll();
    }

    @Test(groups = {"regression"},
            description = "TC_CHK_05 - Order total on the confirmation step")
    public void orderTotalOnConfirmationStep() {
        CheckoutObject data = XmlDataLoader.loadDataSet(DATA_FILE, "guestOrder", CheckoutObject.class);
        data.setEmail(TestDataGenerator.uniqueEmail());
        CartPage cartPage = new ShopFlows(getDriver()).addProductToCartAndOpenCart(data.getCategory(), data.getProductName());
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
        CheckoutObject data = XmlDataLoader.loadDataSet(DATA_FILE, "guestOrder", CheckoutObject.class);
        data.setEmail(TestDataGenerator.uniqueEmail());
        CheckoutPage checkoutPage = new ShopFlows(getDriver()).startGuestCheckout(data.getCategory(), data.getProductName());
        checkoutPage.goToConfirmOrderStep(data);
        checkoutPage.confirmOrder();
        new OrderCompletedPage(getDriver()).getSuccessMessage();

        HeaderPage headerPage = new HeaderPage(getDriver());
        Assert.assertEquals(headerPage.getCartQuantity(), 0, "Wrong number in the header");
        headerPage.openShoppingCart();
        Assert.assertEquals(new CartPage(getDriver()).getEmptyCartMessage(),
                XmlDataLoader.loadDataSet(DATA_FILE, "emptyCartMessage", CheckoutObject.class).getExpectedMessage());
    }
}
