package objectdata;

/**
 * One data set from LoginData.xml.
 */
public class LoginObject {

    private String email;
    private String password;
    private String expectedMessage;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getExpectedMessage() {
        return expectedMessage;
    }
}
