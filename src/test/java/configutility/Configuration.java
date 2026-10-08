package configutility;

/**
 * Plain object that holds the values read from GeneralConfiguration.xml.
 */
public class Configuration {

    private String baseUrl;
    private String browser;
    private boolean headless;
    private int explicitWaitSeconds;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }

    public int getExplicitWaitSeconds() {
        return explicitWaitSeconds;
    }

    public void setExplicitWaitSeconds(int explicitWaitSeconds) {
        this.explicitWaitSeconds = explicitWaitSeconds;
    }
}
