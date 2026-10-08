package configutility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Reads src/test/resources/GeneralConfiguration.xml once and keeps the result in memory.
 * "browser" and "headless" can be overridden from the command line:
 * mvn test -Dbrowser=edge -Dheadless=true
 */
public class ConfigurationReader {

    private static final String CONFIG_PATH = "src/test/resources/GeneralConfiguration.xml";
    private static Configuration configuration;

    public static synchronized Configuration getConfiguration() {
        if (configuration == null) {
            configuration = readConfig(CONFIG_PATH);
        }
        return configuration;
    }

    public static Configuration readConfig(String filePath) {
        try {
            File file = new File(filePath);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();

            Configuration config = new Configuration();
            config.setBaseUrl(getText(root, "baseUrl"));
            // A value given with -D on the command line wins over the one from the XML file
            config.setBrowser(System.getProperty("browser", getText(root, "browser")).toLowerCase());
            config.setHeadless(Boolean.parseBoolean(System.getProperty("headless", getText(root, "headless"))));
            config.setExplicitWaitSeconds(Integer.parseInt(getText(root, "explicitWaitSeconds")));
            return config;
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Could not read configuration file " + filePath + ": " + e.getMessage(), e);
        }
    }

    private static String getText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            throw new IllegalStateException("Missing <" + tagName + "> element in " + CONFIG_PATH);
        }
        return nodes.item(0).getTextContent().trim();
    }
}
