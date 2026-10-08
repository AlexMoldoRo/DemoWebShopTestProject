package tests;

import helpermethods.TestDataGenerator;
import objectdata.RegisterObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.HeaderPage;
import pages.RegisterPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.Map;

public class RegisterTest extends TestBasePage {

    private Map<String, RegisterObject> registerData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {
        registerData = XmlDataLoader.loadData("RegisterData.xml", RegisterObject.class);
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_REG_01 - Register with valid data")
    public void registerWithValidData() {
        RegisterObject data = registerData.get("validUser");
        data.setEmail(TestDataGenerator.uniqueEmail());

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickRegisterLink();

        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertEquals(registerPage.getRegistrationResultMessage(), data.getExpectedMessage());
        // After registration the customer is logged in automatically
        Assert.assertEquals(headerPage.getLoggedInEmail(), data.getEmail());
    }

    @Test(groups = {"regression"},
            description = "TC_REG_02 - Register with all mandatory fields empty")
    public void registerWithEmptyMandatoryFields() {
        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickRegisterLink();

        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.clickRegisterButton();

        Assert.assertEquals(registerPage.getFirstNameError(), "First name is required.");
        Assert.assertEquals(registerPage.getLastNameError(), "Last name is required.");
        Assert.assertEquals(registerPage.getEmailError(), "Email is required.");
        Assert.assertEquals(registerPage.getPasswordError(), "Password is required.");
        Assert.assertEquals(registerPage.getConfirmPasswordError(), "Password is required.");
    }

    @Test(groups = {"regression"},
            description = "TC_REG_03 - Register with an invalid email format")
    public void registerWithInvalidEmailFormat() {
        RegisterObject data = registerData.get("invalidEmail");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickRegisterLink();

        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertEquals(registerPage.getEmailError(), data.getExpectedMessage());
    }

    @Test(groups = {"regression"},
            description = "TC_REG_04 - Register with a password shorter than 6 characters")
    public void registerWithShortPassword() {
        RegisterObject data = registerData.get("shortPassword");
        data.setEmail(TestDataGenerator.uniqueEmail());

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickRegisterLink();

        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertEquals(registerPage.getPasswordError(), data.getExpectedMessage());
    }

    @Test(groups = {"regression"},
            description = "TC_REG_05 - Register with a different confirmation password")
    public void registerWithMismatchedPasswords() {
        RegisterObject data = registerData.get("passwordMismatch");
        data.setEmail(TestDataGenerator.uniqueEmail());

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickRegisterLink();

        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertEquals(registerPage.getConfirmPasswordError(), data.getExpectedMessage());
    }

    @Test(groups = {"regression"},
            description = "TC_REG_06 - Register with an email that is already registered")
    public void registerWithExistingEmail() {
        RegisterObject data = registerData.get("existingEmail");
        data.setEmail(TestDataGenerator.uniqueEmail());

        HeaderPage headerPage = new HeaderPage(getDriver());
        RegisterPage registerPage = new RegisterPage(getDriver());

        // Precondition: an account with this email exists
        headerPage.clickRegisterLink();
        registerPage.registerUser(data);
        Assert.assertEquals(headerPage.getLoggedInEmail(), data.getEmail(), "Precondition failed: user not registered");
        headerPage.clickLogoutLink();

        // Test: register again with the same email
        headerPage.clickRegisterLink();
        registerPage.registerUser(data);

        Assert.assertTrue(registerPage.getSummaryError().contains(data.getExpectedMessage()),
                "Actual message: " + registerPage.getSummaryError());
    }
}
