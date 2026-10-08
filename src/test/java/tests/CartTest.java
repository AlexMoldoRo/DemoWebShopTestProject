package tests;

import objectdata.CartObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CategoryPage;
import pages.HeaderPage;
import pages.NotificationBar;
import pages.ProductPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.Map;

/**
 * Every test starts with a new browser, so the cart of the guest customer is always empty at the start.
 */
public class CartTest extends TestBasePage {

    private Map<String, CartObject> cartData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {
        cartData = XmlDataLoader.loadData("CartData.xml", CartObject.class);
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_CRT_01 - Add a product to the cart from the product page")
    public void addProductToCartFromProductPage() {
        CartObject data = cartData.get("addToCart");

        ProductPage productPage = openProductPage(data.getCategory(), data.getProductName());
        double price = productPage.getProductPrice();
        productPage.clickAddToCart();

        NotificationBar notificationBar = new NotificationBar(getDriver());
        Assert.assertEquals(notificationBar.getMessage(), data.getExpectedMessage());
        notificationBar.close();

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.openShoppingCart();

        CartPage cartPage = new CartPage(getDriver());
        Assert.assertEquals(headerPage.getCartQuantity(), 1, "Wrong number in the header");
        Assert.assertTrue(cartPage.containsProduct(data.getProductName()), "The product is not in the cart");
        Assert.assertEquals(cartPage.getQuantity(data.getProductName()), 1, "Wrong quantity");
        Assert.assertEquals(cartPage.getUnitPrice(data.getProductName()), price, "Wrong price in the cart");
    }

    @Test(groups = {"regression"},
            description = "TC_CRT_02 - Add a product to the cart from the category listing")
    public void addProductToCartFromList() {
        CartObject data = cartData.get("addFromList");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        categoryPage.addProductToCartFromList(data.getProductName());

        NotificationBar notificationBar = new NotificationBar(getDriver());
        Assert.assertEquals(notificationBar.getMessage(), data.getExpectedMessage());
        notificationBar.close();

        headerPage.openShoppingCart();
        CartPage cartPage = new CartPage(getDriver());
        Assert.assertEquals(headerPage.getCartQuantity(), 1, "Wrong number in the header");
        Assert.assertTrue(cartPage.containsProduct(data.getProductName()), "The product is not in the cart");
    }

    @Test(groups = {"regression"},
            description = "TC_CRT_03 - Update the quantity of a product")
    public void updateProductQuantity() {
        CartObject data = cartData.get("updateQuantity");
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());

        double unitPrice = cartPage.getUnitPrice(data.getProductName());
        cartPage.updateQuantity(data.getProductName(), data.getQuantity());

        int quantity = Integer.parseInt(data.getQuantity());
        Assert.assertEquals(cartPage.getQuantity(data.getProductName()), quantity, "Wrong quantity");
        Assert.assertEquals(cartPage.getLineTotal(data.getProductName()), unitPrice * quantity, "Wrong line total");
        Assert.assertEquals(cartPage.getSubTotal(), unitPrice * quantity, "Wrong Sub-Total");
    }

    @Test(groups = {"regression"},
            description = "TC_CRT_04 - Remove a product from the cart")
    public void removeProductFromCart() {
        CartObject data = cartData.get("removeFromCart");
        CartPage cartPage = addProductAndOpenCart(data.getCategory(), data.getProductName());

        cartPage.removeProduct(data.getProductName());

        Assert.assertEquals(cartPage.getEmptyCartMessage(), data.getEmptyMessage());
        Assert.assertEquals(new HeaderPage(getDriver()).getCartQuantity(), 0, "Wrong number in the header");
    }

    @Test(groups = {"regression"},
            description = "TC_CRT_05 - Sub-total with several products")
    public void subTotalWithSeveralProducts() {
        CartObject data = cartData.get("severalProducts");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        NotificationBar notificationBar = new NotificationBar(getDriver());
        categoryPage.addProductToCartFromList(data.getProductName());
        notificationBar.close();
        categoryPage.addProductToCartFromList(data.getSecondProductName());
        notificationBar.close();

        headerPage.openShoppingCart();
        CartPage cartPage = new CartPage(getDriver());

        Assert.assertTrue(cartPage.containsProduct(data.getProductName()), data.getProductName() + " is not in the cart");
        Assert.assertTrue(cartPage.containsProduct(data.getSecondProductName()), data.getSecondProductName() + " is not in the cart");
        double expectedSubTotal = cartPage.getLineTotal(data.getProductName())
                + cartPage.getLineTotal(data.getSecondProductName());
        Assert.assertEquals(cartPage.getSubTotal(), expectedSubTotal, "Sub-Total is not the sum of the lines");
    }

    @Test(groups = {"regression"},
            description = "TC_CRT_06 - Add a product with an invalid quantity")
    public void addProductWithInvalidQuantity() {
        CartObject data = cartData.get("invalidQuantity");

        ProductPage productPage = openProductPage(data.getCategory(), data.getProductName());
        productPage.enterQuantity(data.getQuantity());
        productPage.clickAddToCart();

        NotificationBar notificationBar = new NotificationBar(getDriver());
        Assert.assertTrue(notificationBar.isError(), "The notification is not an error");
        Assert.assertEquals(notificationBar.getMessage(), data.getExpectedMessage());
        notificationBar.close();

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.openShoppingCart();
        Assert.assertEquals(new CartPage(getDriver()).getEmptyCartMessage(), data.getEmptyMessage());
        Assert.assertEquals(headerPage.getCartQuantity(), 0, "Wrong number in the header");
    }

    private ProductPage openProductPage(String category, String productName) {
        new HeaderPage(getDriver()).clickTopMenuCategory(category);
        new CategoryPage(getDriver()).openProduct(productName);
        return new ProductPage(getDriver());
    }

    // Precondition shared by several tests: the cart contains one product
    private CartPage addProductAndOpenCart(String category, String productName) {
        ProductPage productPage = openProductPage(category, productName);
        productPage.clickAddToCart();
        NotificationBar notificationBar = new NotificationBar(getDriver());
        notificationBar.getMessage();
        notificationBar.close();
        new HeaderPage(getDriver()).openShoppingCart();
        CartPage cartPage = new CartPage(getDriver());
        Assert.assertTrue(cartPage.containsProduct(productName), "Precondition failed: the product is not in the cart");
        return cartPage;
    }
}
