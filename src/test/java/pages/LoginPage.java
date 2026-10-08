package pages;

import logger.LoggerUtility;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "Email")
    private WebElement emailField;

    @FindBy(id = "Password")
    private WebElement passwordField;

    @FindBy(css = "input.login-button")
    private WebElement loginButton;

    @FindBy(css = ".validation-summary-errors")
    private WebElement loginError;

    public void login(String email, String password) {
        helperMethods.enterText(emailField, email);
        helperMethods.enterText(passwordField, password);
        helperMethods.clickOnElement(loginButton);
        LoggerUtility.infoLog("Submitted the login form for " + email);
    }

    public String getLoginError() {
        return helperMethods.getElementText(loginError);
    }
}
