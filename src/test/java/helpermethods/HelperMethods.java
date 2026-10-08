package helpermethods;

import configutility.ConfigurationReader;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrappers over the Selenium actions. Every action first waits for the element (explicit wait),
 * so the tests never need Thread.sleep.
 */
public class HelperMethods {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public HelperMethods(WebDriver driver) {
        this.driver = driver;
        int timeout = ConfigurationReader.getConfiguration().getExplicitWaitSeconds();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public void waitElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    public void clickOnElement(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public void enterText(WebElement element, String text) {
        waitElementVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    public String getElementText(WebElement element) {
        waitElementVisible(element);
        return element.getText().trim();
    }

    /**
     * Answers right away, without waiting: used to check that something is NOT on the page.
     */
    public boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public void selectByText(WebElement element, String text) {
        waitElementVisible(element);
        new Select(element).selectByVisibleText(text);
    }

    /**
     * Returns the text of every element from a list (for example: all product titles).
     */
    public List<String> getElementsTexts(List<WebElement> elements) {
        wait.until(ExpectedConditions.visibilityOfAllElements(elements));
        List<String> texts = new ArrayList<>();
        for (WebElement element : elements) {
            texts.add(element.getText().trim());
        }
        return texts;
    }

    /**
     * Used after an action that reloads the page without a click on a link (a drop-down that
     * changes the URL): waits for the new URL and for the new page to finish loading.
     */
    public void waitUrlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
        wait.until(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState")));
    }

    public void waitElementInvisible(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    /**
     * Converts a price as displayed by the shop ("1,590.00") into a number.
     */
    public double getPrice(WebElement element) {
        return Double.parseDouble(getElementText(element).replace(",", ""));
    }

    /**
     * Waits, up to the given number of seconds, until a JavaScript expression of the page is true.
     * Used for pages that load content in the background (AJAX), where no element tells
     * reliably that the loading is over.
     */
    public void waitJavascriptCondition(String javascriptExpression, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(
                d -> Boolean.TRUE.equals(((JavascriptExecutor) d).executeScript("return " + javascriptExpression + ";")));
    }

    public void waitUrlContains(String urlPart, int seconds) {
        new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(ExpectedConditions.urlContains(urlPart));
    }

    public String getAlertTextAndAccept() {
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        alert.accept();
        return text;
    }
}
