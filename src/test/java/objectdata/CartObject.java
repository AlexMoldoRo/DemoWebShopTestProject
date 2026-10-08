package objectdata;

/**
 * One data set from CartData.xml.
 */
public class CartObject {

    private String category;
    private String productName;
    private String secondProductName;
    private String quantity;
    private String expectedMessage;
    private String emptyMessage;

    public String getCategory() {
        return category;
    }

    public String getProductName() {
        return productName;
    }

    public String getSecondProductName() {
        return secondProductName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getExpectedMessage() {
        return expectedMessage;
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }
}
