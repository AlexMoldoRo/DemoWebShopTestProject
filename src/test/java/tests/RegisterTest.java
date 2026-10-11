package tests;

import flows.ShopFlows;
import helpermethods.TestDataGenerator;
import objectdata.RegisterObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import pages.HeaderPage;
import pages.RegisterPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.Map;

public class RegisterTest extends TestBasePage {

    private static final String DATA_FILE = "RegisterData.xml";

    @Test(groups = {"smoke", "regression"},
            description = "TC_REG_01 - Register with valid data")
    public void registerWithValidData() {
        RegisterObject data = XmlDataLoader.loadDataSet(DATA_FILE, "validUser", RegisterObject.class);
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
        RegisterObject data = XmlDataLoader.loadDataSet(DATA_FILE, "emptyFields", RegisterObject.class);

        new HeaderPage(getDriver()).clickRegisterLink();
        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.clickRegisterButton();

        // SoftAssert checks every field and reports all wrong messages, not only the first one
        SoftAssert softAssert = new SoftAssert();
        for (Map.Entry<String, String> expected : data.getExpectedErrors().entrySet()) {
            softAssert.assertEquals(registerPage.getFieldError(expected.getKey()), expected.getValue(),
                    "Wrong message for " + expected.getKey());
        }
        softAssert.assertAll();
    }

    @Test(groups = {"regression"},
            description = "TC_REG_03 - Register with an invalid email format")
    public void registerWithInvalidEmailFormat() {
        RegisterObject data = XmlDataLoader.loadDataSet(DATA_FILE, "invalidEmail", RegisterObject.class);

        new HeaderPage(getDriver()).clickRegisterLink();
        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertEquals(registerPage.getFieldError("Email"), data.getExpectedMessage());
    }

    @Test(groups = {"regression"},
            description = "TC_REG_04 - Register with a password shorter than 6 characters")
    public void registerWithShortPassword() {
        RegisterObject data = XmlDataLoader.loadDataSet(DATA_FILE, "shortPassword", RegisterObject.class);
        data.setEmail(TestDataGenerator.uniqueEmail());

        new HeaderPage(getDriver()).clickRegisterLink();
        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertEquals(registerPage.getFieldError("Password"), data.getExpectedMessage());
    }

    @Test(groups = {"regression"},
            description = "TC_REG_05 - Register with a different confirmation password")
    public void registerWithMismatchedPasswords() {
        RegisterObject data = XmlDataLoader.loadDataSet(DATA_FILE, "passwordMismatch", RegisterObject.class);
        data.setEmail(TestDataGenerator.uniqueEmail());

        new HeaderPage(getDriver()).clickRegisterLink();
        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertEquals(registerPage.getFieldError("ConfirmPassword"), data.getExpectedMessage());
    }

    @Test(groups = {"regression"},
            description = "TC_REG_06 - Register with an email that is already registered")
    public void registerWithExistingEmail() {
        RegisterObject data = XmlDataLoader.loadDataSet(DATA_FILE, "existingEmail", RegisterObject.class);

        // Precondition: an account with this email exists
        new ShopFlows(getDriver()).registerNewUserAndLogOut(data);

        // Test: register again with the same email
        new HeaderPage(getDriver()).clickRegisterLink();
        RegisterPage registerPage = new RegisterPage(getDriver());
        registerPage.registerUser(data);

        Assert.assertTrue(registerPage.getSummaryError().contains(data.getExpectedMessage()),
                "Actual message: " + registerPage.getSummaryError());
    }
}
