package tests;

import helpermethods.TestDataGenerator;
import objectdata.LoginObject;
import objectdata.RegisterObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pages.HeaderPage;
import pages.LoginPage;
import pages.RegisterPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

import java.util.Map;

public class LoginTest extends TestBasePage {

    private Map<String, RegisterObject> registerData;
    private Map<String, LoginObject> loginData;

    @BeforeClass(alwaysRun = true)
    public void loadTestData() {
        registerData = XmlDataLoader.loadData("RegisterData.xml", RegisterObject.class);
        loginData = XmlDataLoader.loadData("LoginData.xml", LoginObject.class);
    }

    @Test(groups = {"smoke", "regression"},
            description = "TC_LOG_01 - Login with valid credentials")
    public void loginWithValidCredentials() {
        RegisterObject user = createRegisteredUser();

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickLoginLink();

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(user.getEmail(), user.getPassword());

        Assert.assertEquals(headerPage.getLoggedInEmail(), user.getEmail());
        Assert.assertTrue(headerPage.isLogoutLinkDisplayed(), "The Log out link is not displayed");
    }

    @Test(groups = {"regression"},
            description = "TC_LOG_02 - Login with a wrong password")
    public void loginWithWrongPassword() {
        RegisterObject user = createRegisteredUser();
        LoginObject data = loginData.get("wrongPassword");

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickLoginLink();

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(user.getEmail(), data.getPassword());

        Assert.assertTrue(loginPage.getLoginError().contains(data.getExpectedMessage()),
                "Actual message: " + loginPage.getLoginError());
        Assert.assertTrue(headerPage.isLoginLinkDisplayed(), "The customer should not be logged in");
    }

    @Test(groups = {"regression"},
            description = "TC_LOG_03 - Login with an email that is not registered")
    public void loginWithUnregisteredEmail() {
        LoginObject data = loginData.get("unregisteredEmail");
        data.setEmail(TestDataGenerator.uniqueEmail());

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickLoginLink();

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(data.getEmail(), data.getPassword());

        Assert.assertTrue(loginPage.getLoginError().contains(data.getExpectedMessage()),
                "Actual message: " + loginPage.getLoginError());
    }

    @Test(groups = {"regression"},
            description = "TC_LOG_04 - Logout")
    public void logout() {
        RegisterObject user = createRegisteredUser();

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickLoginLink();
        new LoginPage(getDriver()).login(user.getEmail(), user.getPassword());
        Assert.assertTrue(headerPage.isLogoutLinkDisplayed(), "Precondition failed: user not logged in");

        headerPage.clickLogoutLink();

        Assert.assertTrue(headerPage.isLoginLinkDisplayed(), "The Log in link is not displayed after logout");
        Assert.assertFalse(headerPage.isLogoutLinkDisplayed(), "The Log out link is still displayed");
    }

    /**
     * Precondition shared by the login tests: a brand new account, created through the UI.
     * The test does not rely on an account that somebody else could change or delete.
     * Registration logs the customer in, so the method logs out before returning.
     */
    private RegisterObject createRegisteredUser() {
        RegisterObject user = registerData.get("validUser");
        user.setEmail(TestDataGenerator.uniqueEmail());

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickRegisterLink();
        new RegisterPage(getDriver()).registerUser(user);
        Assert.assertEquals(headerPage.getLoggedInEmail(), user.getEmail(), "Precondition failed: user not registered");
        headerPage.clickLogoutLink();
        return user;
    }
}
