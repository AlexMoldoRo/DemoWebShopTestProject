package pages;

import logger.LoggerUtility;
import objectdata.RegisterObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class RegisterPage extends BasePage {

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "gender-male")
    private WebElement genderMaleRadio;

    @FindBy(id = "gender-female")
    private WebElement genderFemaleRadio;

    @FindBy(id = "FirstName")
    private WebElement firstNameField;

    @FindBy(id = "LastName")
    private WebElement lastNameField;

    @FindBy(id = "Email")
    private WebElement emailField;

    @FindBy(id = "Password")
    private WebElement passwordField;

    @FindBy(id = "ConfirmPassword")
    private WebElement confirmPasswordField;

    @FindBy(id = "register-button")
    private WebElement registerButton;

    // Errors sent back by the server (for example: the email is already registered)
    @FindBy(css = ".validation-summary-errors")
    private WebElement summaryError;

    // Displayed after a successful registration
    @FindBy(css = ".registration-result-page .result")
    private WebElement registrationResult;

    public void selectGender(String gender) {
        switch (gender) {
            case "Male":
                helperMethods.clickOnElement(genderMaleRadio);
                break;
            case "Female":
                helperMethods.clickOnElement(genderFemaleRadio);
                break;
            default:
                throw new IllegalArgumentException("Unknown gender in the test data: " + gender);
        }
    }

    public void fillRegisterForm(RegisterObject data) {
        selectGender(data.getGender());
        helperMethods.enterText(firstNameField, data.getFirstName());
        helperMethods.enterText(lastNameField, data.getLastName());
        helperMethods.enterText(emailField, data.getEmail());
        helperMethods.enterText(passwordField, data.getPassword());
        helperMethods.enterText(confirmPasswordField, data.getConfirmPassword());
        LoggerUtility.infoLog("Filled in the register form for " + data.getEmail());
    }

    public void clickRegisterButton() {
        helperMethods.clickOnElement(registerButton);
        LoggerUtility.infoLog("Clicked on the Register button");
    }

    // The whole flow in one call, for the tests that only need "a registered user"
    public void registerUser(RegisterObject data) {
        fillRegisterForm(data);
        clickRegisterButton();
    }

    public String getRegistrationResultMessage() {
        return helperMethods.getElementText(registrationResult);
    }

    /**
     * Validation message displayed next to a field, for example "Email" -> "Wrong email".
     * The field name is the one used by the page: FirstName, LastName, Email, Password, ConfirmPassword.
     */
    public String getFieldError(String fieldName) {
        WebElement error = driver.findElement(By.cssSelector("span[data-valmsg-for='" + fieldName + "']"));
        return helperMethods.getElementText(error);
    }

    public String getSummaryError() {
        return helperMethods.getElementText(summaryError);
    }
}
