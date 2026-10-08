package pages;

import logger.LoggerUtility;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class SearchPage extends BasePage {

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "Q")
    private WebElement keywordField;

    @FindBy(id = "As")
    private WebElement advancedSearchCheckbox;

    @FindBy(id = "Cid")
    private WebElement categoryDropdown;

    @FindBy(css = ".search-input input.search-button")
    private WebElement searchButton;

    @FindBy(css = ".search-results .product-item .product-title a")
    private List<WebElement> productTitles;

    @FindBy(css = ".search-results .result")
    private WebElement noResultsMessage;

    @FindBy(css = ".search-results .warning")
    private WebElement warningMessage;

    public void advancedSearch(String keyword, String category) {
        helperMethods.enterText(keywordField, keyword);
        // The category drop-down is hidden until Advanced search is checked
        if (!advancedSearchCheckbox.isSelected()) {
            helperMethods.clickOnElement(advancedSearchCheckbox);
        }
        helperMethods.selectByText(categoryDropdown, category);
        helperMethods.clickOnElement(searchButton);
        LoggerUtility.infoLog("Advanced search for '" + keyword + "' in category " + category);
    }

    public List<String> getProductTitles() {
        return helperMethods.getElementsTexts(productTitles);
    }

    public int getNumberOfProducts() {
        return productTitles.size();
    }

    public String getNoResultsMessage() {
        return helperMethods.getElementText(noResultsMessage);
    }

    public String getWarningMessage() {
        return helperMethods.getElementText(warningMessage);
    }
}
