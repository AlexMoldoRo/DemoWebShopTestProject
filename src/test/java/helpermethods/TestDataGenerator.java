package helpermethods;

import java.util.UUID;

public class TestDataGenerator {

    /**
     * The shop does not allow two accounts with the same email, so each run needs a new one.
     * example.com is a domain reserved for testing: no real mailbox can exist on it.
     */
    public static String uniqueEmail() {
        String randomPart = UUID.randomUUID().toString().substring(0, 8);
        return "dws.auto." + randomPart + "@example.com";
    }
}
