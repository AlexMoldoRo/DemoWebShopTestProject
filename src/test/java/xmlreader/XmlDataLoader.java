package xmlreader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the data sets of an XML file from src/test/resources/testData into objects.
 * Each child of <dataSets> becomes one object; the name of the child is the key in the map.
 * A tag is copied into the field that has exactly the same name.
 */
public class XmlDataLoader {

    private static final String TEST_DATA_FOLDER = "src/test/resources/testData/";

    public static <T> Map<String, T> loadData(String fileName, Class<T> clazz) {
        Map<String, T> dataMap = new HashMap<>();

        try {
            File file = new File(TEST_DATA_FOLDER + fileName);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("dataSets").item(0).getChildNodes();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element element = (Element) node;
                T obj = clazz.getDeclaredConstructor().newInstance();

                for (Field field : clazz.getDeclaredFields()) {
                    NodeList matches = element.getElementsByTagName(field.getName());
                    if (matches.getLength() == 0) {
                        continue;
                    }
                    String value = matches.item(0).getTextContent().trim();
                    field.setAccessible(true);

                    if (field.getType().equals(List.class)) {
                        field.set(obj, Arrays.asList(value.split(",\\s*")));
                    } else if (field.getType().equals(int.class)) {
                        field.set(obj, Integer.parseInt(value));
                    } else {
                        field.set(obj, value);
                    }
                }
                dataMap.put(element.getNodeName(), obj);
            }
        } catch (Exception e) {
            // Fail loudly: a test must not continue with missing data
            throw new IllegalStateException("Could not load test data from " + fileName + ": " + e.getMessage(), e);
        }

        return dataMap;
    }
}
