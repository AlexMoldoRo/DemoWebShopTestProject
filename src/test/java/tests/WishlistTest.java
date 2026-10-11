package tests;

import flows.ShopFlows;
import objectdata.CartObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.HeaderPage;
import pages.NotificationBar;
import pages.ProductPage;
import pages.WishlistPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

public class WishlistTest extends TestBasePage {

    private static final String DATA_FILE = "CartData.xml";

    @Test(groups = {"smoke", "regression"},
            description = "TC_WSH_01 - Add a product to the wishlist")
    public void addProductToWishlist() {
        CartObject data = XmlDataLoader.loadDataSet(DATA_FILE, "addToWishlist", CartObject.class);

        ProductPage productPage = new ShopFlows(getDriver()).openProductPage(data.getCategory(), data.getProductName());
        productPage.clickAddToWishlist();

        NotificationBar notificationBar = new NotificationBar(getDriver());
        Assert.assertEquals(notificationBar.getMessage(), data.getExpectedMessage());
        notificationBar.close();

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.openWishlist();

        Assert.assertEquals(headerPage.getWishlistQuantity(), 1, "Wrong number in the header");
        Assert.assertTrue(new WishlistPage(getDriver()).containsProduct(data.getProductName()),
                "The product is not in the wishlist");
    }

    @Test(groups = {"regression"},
            description = "TC_WSH_02 - Move a product from the wishlist to the cart")
    public void moveProductFromWishlistToCart() {
        CartObject data = XmlDataLoader.loadDataSet(DATA_FILE, "addToWishlist", CartObject.class);
        WishlistPage wishlistPage = new ShopFlows(getDriver())
                .addProductToWishlistAndOpenWishlist(data.getCategory(), data.getProductName());

        wishlistPage.moveProductToCart(data.getProductName());

        // The shop opens the shopping cart after the move
        CartPage cartPage = new CartPage(getDriver());
        Assert.assertTrue(cartPage.containsProduct(data.getProductName()), "The product is not in the cart");

        HeaderPage headerPage = new HeaderPage(getDriver());
        Assert.assertEquals(headerPage.getWishlistQuantity(), 0, "The product is still counted in the wishlist");
        headerPage.openWishlist();
        Assert.assertEquals(wishlistPage.getEmptyWishlistMessage(), data.getEmptyMessage());
    }

    @Test(groups = {"regression"},
            description = "TC_WSH_03 - Remove a product from the wishlist")
    public void removeProductFromWishlist() {
        CartObject data = XmlDataLoader.loadDataSet(DATA_FILE, "addToWishlist", CartObject.class);
        WishlistPage wishlistPage = new ShopFlows(getDriver())
                .addProductToWishlistAndOpenWishlist(data.getCategory(), data.getProductName());

        wishlistPage.removeProduct(data.getProductName());

        Assert.assertEquals(wishlistPage.getEmptyWishlistMessage(), data.getEmptyMessage());
        Assert.assertEquals(new HeaderPage(getDriver()).getWishlistQuantity(), 0, "Wrong number in the header");
    }
}
