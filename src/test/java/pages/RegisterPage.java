package pages;

import logger.LoggerUtility;
import objectdata.RegisterObject;
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

    // Validation messages displayed next to each field
    @FindBy(css = "span[data-valmsg-for='FirstName']")
    private WebElement firstNameError;

    @FindBy(css = "span[data-valmsg-for='LastName']")
    private WebElement lastNameError;

    @FindBy(css = "span[data-valmsg-for='Email']")
    private WebElement emailError;

    @FindBy(css = "span[data-valmsg-for='Password']")
    private WebElement passwordError;

    @FindBy(css = "span[data-valmsg-for='ConfirmPassword']")
    private WebElement confirmPasswordError;

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

    public String getFirstNameError() {
        return helperMethods.getElementText(firstNameError);
    }

    public String getLastNameError() {
        return helperMethods.getElementText(lastNameError);
    }

    public String getEmailError() {
        return helperMethods.getElementText(emailError);
    }

    public String getPasswordError() {
        return helperMethods.getElementText(passwordError);
    }

    public String getConfirmPasswordError() {
        return helperMethods.getElementText(confirmPasswordError);
    }

    public String getSummaryError() {
        return helperMethods.getElementText(summaryError);
    }
}
