package objectdata;

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
}
