package pages;

import logger.LoggerUtility;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * My account of a logged in customer (opened from the email link in the header).
 */
public class MyAccountPage extends BasePage {

    public MyAccountPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(css = ".block-account-navigation a[href='/customer/orders']")
    private WebElement ordersLink;

    @FindBy(css = ".order-list")
    private WebElement orderList;

    public void openOrders() {
        helperMethods.clickOnElement(ordersLink);
        LoggerUtility.infoLog("Opened My account > Orders");
    }

    public String getOrderListText() {
        return helperMethods.getElementText(orderList);
    }
}
