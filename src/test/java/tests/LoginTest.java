package tests;

import flows.ShopFlows;
import helpermethods.TestDataGenerator;
import objectdata.LoginObject;
import objectdata.RegisterObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HeaderPage;
import pages.LoginPage;
import shareddata.TestBasePage;
import xmlreader.XmlDataLoader;

public class LoginTest extends TestBasePage {

    @Test(groups = {"smoke", "regression"},
            description = "TC_LOG_01 - Login with valid credentials")
    public void loginWithValidCredentials() {
        // Precondition: a new account, created through the UI and logged out again
        RegisterObject user = new ShopFlows(getDriver()).registerNewUserAndLogOut(
                XmlDataLoader.loadDataSet("RegisterData.xml", "validUser", RegisterObject.class));

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
        // Precondition: a new account, created through the UI and logged out again
        RegisterObject user = new ShopFlows(getDriver()).registerNewUserAndLogOut(
                XmlDataLoader.loadDataSet("RegisterData.xml", "validUser", RegisterObject.class));
        LoginObject data = XmlDataLoader.loadDataSet("LoginData.xml", "wrongPassword", LoginObject.class);

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
        LoginObject data = XmlDataLoader.loadDataSet("LoginData.xml", "unregisteredEmail", LoginObject.class);
        data.setEmail(TestDataGenerator.uniqueEmail());

        new HeaderPage(getDriver()).clickLoginLink();

        LoginPage loginPage = new LoginPage(getDriver());
        loginPage.login(data.getEmail(), data.getPassword());

        Assert.assertTrue(loginPage.getLoginError().contains(data.getExpectedMessage()),
                "Actual message: " + loginPage.getLoginError());
    }

    @Test(groups = {"regression"},
            description = "TC_LOG_04 - Logout")
    public void logout() {
        // Precondition: a new account, created through the UI and logged out again
        RegisterObject user = new ShopFlows(getDriver()).registerNewUserAndLogOut(
                XmlDataLoader.loadDataSet("RegisterData.xml", "validUser", RegisterObject.class));

        HeaderPage headerPage = new HeaderPage(getDriver());
        headerPage.clickLoginLink();
        new LoginPage(getDriver()).login(user.getEmail(), user.getPassword());
        Assert.assertTrue(headerPage.isLogoutLinkDisplayed(), "Precondition failed: user not logged in");

        headerPage.clickLogoutLink();

        Assert.assertTrue(headerPage.isLoginLinkDisplayed(), "The Log in link is not displayed after logout");
        Assert.assertFalse(headerPage.isLogoutLinkDisplayed(), "The Log out link is still displayed");
    }
}
