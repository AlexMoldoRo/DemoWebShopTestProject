package tests;

import objectdata.SearchObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HeaderPage;
import pages.SearchPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.List;

public class SearchTest extends TestBasePage {

    private static final String DATA_FILE = "SearchData.xml";

    @Test(groups = {"smoke", "regression"},
            description = "TC_SRC_01 - Search by an existing keyword")
    public void searchByExistingKeyword() {
        SearchObject data = XmlDataLoader.loadDataSet(DATA_FILE, "existingKeyword", SearchObject.class);

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.searchFor(data.getKeyword());

        SearchPage searchPage = new SearchPage(getDriver());
        List<String> titles = searchPage.getProductTitles();

        Assert.assertFalse(titles.isEmpty(), "No product was found for '" + data.getKeyword() + "'");
        for (String title : titles) {
            Assert.assertTrue(title.toLowerCase().contains(data.getKeyword().toLowerCase()),
                    "Product '" + title + "' does not contain the keyword '" + data.getKeyword() + "'");
        }
    }

    @Test(groups = {"regression"},
            description = "TC_SRC_02 - Search by a keyword with no results")
    public void searchByKeywordWithNoResults() {
        SearchObject data = XmlDataLoader.loadDataSet(DATA_FILE, "noResultsKeyword", SearchObject.class);

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.searchFor(data.getKeyword());

        SearchPage searchPage = new SearchPage(getDriver());

        Assert.assertEquals(searchPage.getNoResultsMessage(), data.getExpectedMessage());
        Assert.assertEquals(searchPage.getNumberOfProducts(), 0, "Products are listed although none was expected");
    }

    @Test(groups = {"regression"},
            description = "TC_SRC_03 - Search with an empty keyword")
    public void searchWithEmptyKeyword() {
        SearchObject data = XmlDataLoader.loadDataSet(DATA_FILE, "emptyKeyword", SearchObject.class);

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickSearchButton();

        Assert.assertEquals(headerPage.getSearchAlertTextAndAccept(), data.getExpectedMessage());
        Assert.assertFalse(getDriver().getCurrentUrl().contains("/search"), "The search was executed");
    }

    @Test(groups = {"regression"},
            description = "TC_SRC_04 - Search by a keyword shorter than 3 characters")
    public void searchByShortKeyword() {
        SearchObject data = XmlDataLoader.loadDataSet(DATA_FILE, "shortKeyword", SearchObject.class);

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.searchFor(data.getKeyword());

        SearchPage searchPage = new SearchPage(getDriver());

        Assert.assertEquals(searchPage.getWarningMessage(), data.getExpectedMessage());
        Assert.assertEquals(searchPage.getNumberOfProducts(), 0, "Products are listed although none was expected");
    }

    @Test(groups = {"regression"},
            description = "TC_SRC_05 - Advanced search limited to a category")
    public void advancedSearchByCategory() {
        SearchObject otherCategory = XmlDataLoader.loadDataSet(DATA_FILE, "advancedOtherCategory", SearchObject.class);
        SearchObject sameCategory = XmlDataLoader.loadDataSet(DATA_FILE, "advancedSameCategory", SearchObject.class);

        // Opens the Search page, where the advanced search is
        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.searchFor(otherCategory.getKeyword());

        // The keyword exists, but not in this category: nothing is found
        SearchPage searchPage = new SearchPage(getDriver());
        searchPage.advancedSearch(otherCategory.getKeyword(), otherCategory.getCategory());
        Assert.assertEquals(searchPage.getNoResultsMessage(), otherCategory.getExpectedMessage());

        // The same keyword in the category of the products: they are found
        searchPage.advancedSearch(sameCategory.getKeyword(), sameCategory.getCategory());
        List<String> titles = searchPage.getProductTitles();
        Assert.assertFalse(titles.isEmpty(), "No product was found in " + sameCategory.getCategory());
        for (String title : titles) {
            Assert.assertTrue(title.toLowerCase().contains(sameCategory.getKeyword().toLowerCase()),
                    "Product '" + title + "' does not contain the keyword");
        }
    }
}
