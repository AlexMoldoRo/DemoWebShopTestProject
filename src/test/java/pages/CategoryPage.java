package pages;

import logger.LoggerUtility;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.ArrayList;
import java.util.List;

/**
 * Any page that lists the products of a category (Books, Desktops, ...).
 */
public class CategoryPage extends BasePage {

    public CategoryPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = ".page-title h1")
    private WebElement pageTitle;

    @FindBy(css = ".breadcrumb .current-item")
    private WebElement breadcrumbCurrentItem;

    @FindBy(css = ".product-item .product-title a")
    private List<WebElement> productTitles;

    // The price the customer pays (the crossed out old price has a different class)
    @FindBy(css = ".product-item .actual-price")
    private List<WebElement> productPrices;

    @FindBy(id = "products-orderby")
    private WebElement sortByDropdown;

    @FindBy(id = "products-pagesize")
    private WebElement pageSizeDropdown;

    @FindBy(css = ".pager li.next-page")
    private WebElement nextPageLink;

    public String getPageTitle() {
        return helperMethods.getElementText(pageTitle);
    }

    public String getBreadcrumbCurrentItem() {
        return helperMethods.getElementText(breadcrumbCurrentItem);
    }

    public List<String> getProductTitles() {
        return helperMethods.getElementsTexts(productTitles);
    }

    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        for (String price : helperMethods.getElementsTexts(productPrices)) {
            prices.add(Double.parseDouble(price));
        }
        return prices;
    }

    public int getNumberOfProducts() {
        return productTitles.size();
    }

    // Choosing an option reloads the page with "orderby" in the URL
    public void sortBy(String option) {
        helperMethods.selectByText(sortByDropdown, option);
        helperMethods.waitUrlContains("orderby=");
        LoggerUtility.infoLog("Sorted the products by " + option);
    }

    public void selectPageSize(String size) {
        helperMethods.selectByText(pageSizeDropdown, size);
        helperMethods.waitUrlContains("pagesize=");
        LoggerUtility.infoLog("Selected " + size + " products per page");
    }

    public void filterByPrice(String priceRange) {
        WebElement filter = driver.findElement(
                By.xpath("//div[contains(@class,'price-range-filter')]//a[normalize-space()='" + priceRange + "']"));
        helperMethods.clickOnElement(filter);
        LoggerUtility.infoLog("Filtered the products by price: " + priceRange);
    }

    public boolean isNextPageLinkDisplayed() {
        return helperMethods.isElementDisplayed(nextPageLink);
    }

    public void openSubCategory(String subCategoryName) {
        WebElement subCategory = driver.findElement(
                By.xpath("//div[@class='sub-category-item']//h2/a[normalize-space()='" + subCategoryName + "']"));
        helperMethods.clickOnElement(subCategory);
        LoggerUtility.infoLog("Opened the sub-category " + subCategoryName);
    }

    public double getPriceOfProduct(String productName) {
        WebElement price = driver.findElement(By.xpath(productBox(productName)
                + "//span[contains(@class,'actual-price')]"));
        return Double.parseDouble(helperMethods.getElementText(price));
    }

    public void openProduct(String productName) {
        WebElement title = driver.findElement(By.xpath(productBox(productName)
                + "//h2[@class='product-title']/a"));
        helperMethods.clickOnElement(title);
        LoggerUtility.infoLog("Opened the product " + productName);
    }

    public void addProductToCartFromList(String productName) {
        WebElement button = driver.findElement(By.xpath(productBox(productName)
                + "//input[contains(@class,'product-box-add-to-cart-button')]"));
        helperMethods.clickOnElement(button);
        LoggerUtility.infoLog("Clicked on Add to cart for " + productName + " in the product list");
    }

    // XPath of the box (picture, title, price, button) of one product from the list
    private String productBox(String productName) {
        return "//div[@class='product-item'][.//h2[@class='product-title']/a[normalize-space()='" + productName + "']]";
    }
}
