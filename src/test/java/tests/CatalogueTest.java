package tests;

import objectdata.CatalogueObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.CategoryPage;
import pages.HeaderPage;
import pages.ProductPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CatalogueTest extends TestBasePage {

    private Map<String, CatalogueObject> catalogueData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {
        catalogueData = XmlDataLoader.loadData("CatalogueData.xml", CatalogueObject.class);
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_CAT_01 - Open a category from the top menu")
    public void openCategoryFromTopMenu() {
        CatalogueObject data = catalogueData.get("books");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());

        Assert.assertEquals(categoryPage.getPageTitle(), data.getCategory());
        // The breadcrumb is displayed in upper case by the CSS, so the case is ignored
        Assert.assertEquals(categoryPage.getBreadcrumbCurrentItem().toLowerCase(), data.getCategory().toLowerCase());
        Assert.assertTrue(categoryPage.getNumberOfProducts() > 0, "No product is listed in " + data.getCategory());
    }

    @Test(groups = {"regression"},
            description = "TC_CAT_02 - Open a sub-category")
    public void openSubCategory() {
        CatalogueObject data = catalogueData.get("desktops");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        categoryPage.openSubCategory(data.getSubCategory());

        Assert.assertEquals(categoryPage.getPageTitle(), data.getSubCategory());
        Assert.assertTrue(categoryPage.getNumberOfProducts() > 0, "No product is listed in " + data.getSubCategory());
    }

    @Test(groups = {"regression"},
            description = "TC_CAT_03 - Sort products by price, low to high")
    public void sortProductsByPriceLowToHigh() {
        CatalogueObject data = catalogueData.get("books");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        categoryPage.sortBy(data.getSortByPrice());

        List<Double> actualPrices = categoryPage.getProductPrices();
        List<Double> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices);

        Assert.assertEquals(actualPrices, expectedPrices, "The prices are not in ascending order");
    }

    @Test(groups = {"regression"},
            description = "TC_CAT_04 - Sort products by name, Z to A")
    public void sortProductsByNameZToA() {
        CatalogueObject data = catalogueData.get("books");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        categoryPage.sortBy(data.getSortByName());

        List<String> actualTitles = categoryPage.getProductTitles();
        List<String> expectedTitles = new ArrayList<>(actualTitles);
        expectedTitles.sort(String.CASE_INSENSITIVE_ORDER.reversed());

        Assert.assertEquals(actualTitles, expectedTitles, "The names are not in descending alphabetical order");
    }

    @Test(groups = {"regression"},
            description = "TC_CAT_05 - Change the number of products per page")
    public void changeNumberOfProductsPerPage() {
        CatalogueObject data = catalogueData.get("books");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        int productsBefore = categoryPage.getNumberOfProducts();
        Assert.assertTrue(productsBefore > data.getPageSize(),
                "Precondition failed: the category needs more than " + data.getPageSize() + " products");

        categoryPage.selectPageSize(String.valueOf(data.getPageSize()));

        Assert.assertEquals(categoryPage.getNumberOfProducts(), data.getPageSize(), "Wrong number of products on the page");
        Assert.assertTrue(categoryPage.isNextPageLinkDisplayed(), "The pager is not displayed");
    }

    @Test(groups = {"regression"},
            description = "TC_CAT_06 - Filter products by price range")
    public void filterProductsByPriceRange() {
        CatalogueObject data = catalogueData.get("books");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        categoryPage.filterByPrice(data.getPriceFilter());

        List<Double> prices = categoryPage.getProductPrices();
        Assert.assertFalse(prices.isEmpty(), "No product is listed after filtering");
        for (Double price : prices) {
            Assert.assertTrue(price < data.getMaxPrice(), "Price " + price + " is not under " + data.getMaxPrice());
        }
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_CAT_07 - Open the product details page")
    public void openProductDetailsPage() {
        CatalogueObject data = catalogueData.get("books");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickTopMenuCategory(data.getCategory());

        CategoryPage categoryPage = new CategoryPage(getDriver());
        double priceInList = categoryPage.getPriceOfProduct(data.getProductName());
        categoryPage.openProduct(data.getProductName());

        ProductPage productPage = new ProductPage(getDriver());

        Assert.assertEquals(productPage.getProductName(), data.getProductName());
        Assert.assertEquals(productPage.getProductPrice(), priceInList, "The price differs from the one in the list");
        Assert.assertTrue(productPage.isAddToCartButtonDisplayed(), "The Add to cart button is not displayed");
        Assert.assertTrue(productPage.isAddToWishlistButtonDisplayed(), "The Add to wishlist button is not displayed");
    }
}
