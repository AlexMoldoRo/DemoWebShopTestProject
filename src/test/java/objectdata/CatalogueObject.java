package objectdata;

/**
 * One data set from CatalogueData.xml.
 */
public class CatalogueObject {

    private String category;
    private String subCategory;
    private String productName;
    private String sortByPrice;
    private String sortByName;
    private String pageSize;
    private String priceFilter;
    private String maxPrice;

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getProductName() {
        return productName;
    }

    public String getSortByPrice() {
        return sortByPrice;
    }

    public String getSortByName() {
        return sortByName;
    }

    public int getPageSize() {
        return Integer.parseInt(pageSize);
    }

    public String getPriceFilter() {
        return priceFilter;
    }

    public double getMaxPrice() {
        return Double.parseDouble(maxPrice);
    }
}
