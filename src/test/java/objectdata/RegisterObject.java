package objectdata;

/**
 * One data set from RegisterData.xml.
 */
public class RegisterObject {

    private String gender;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private String expectedMessage;

    public String getGender() {
        return gender;
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

    // The only setter: the email has to be unique, so it is generated when the test runs
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getExpectedMessage() {
        return expectedMessage;
    }
}
