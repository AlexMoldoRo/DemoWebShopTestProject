package objectdata;

import java.util.Map;

/**
 * One data set from CheckoutData.xml.
 */
public class CheckoutObject {

    private String category;
    private String productName;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String city;
    private String address;
    private String zipCode;
    private String phone;
    private String expectedMessage;
    // Field name -> validation message, for the empty billing address
    private Map<String, String> expectedErrors;

    public String getCategory() {
        return category;
    }

    public String getProductName() {
        return productName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getExpectedMessage() {
        return expectedMessage;
    }

    public Map<String, String> getExpectedErrors() {
        return expectedErrors;
    }
}
