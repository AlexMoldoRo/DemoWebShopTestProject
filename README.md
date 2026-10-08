# Demo Web Shop – UI Test Automation

Automated UI tests for [Demo Web Shop](https://demowebshop.tricentis.com/), a sample e-commerce
application published by Tricentis for test automation practice.

The project covers the main customer journeys of an online shop (registration, login, search,
catalogue, cart, wishlist and checkout) with **37 test cases**, all of them automated with
**Java, Selenium WebDriver and TestNG**, using the **Page Object Model**.

## Tech stack

| Area | Tool |
|---|---|
| Language | Java 25 (LTS) |
| Browser automation | Selenium WebDriver 4.45 |
| Test runner | TestNG 7.12 |
| Build | Maven |
| Reporting | ChainTest (HTML report with screenshots of failed tests) |
| Logging | Log4j 2 (one log file per test, merged at the end of the run) |
| CI | GitHub Actions (headless Chrome) |

## Test coverage

The test cases, with steps, test data and expected results, are in
[`docs/TestCases.xlsx`](docs/TestCases.xlsx).

| Module | Test cases | Test class |
|---|---|---|
| Register | TC_REG_01 – TC_REG_06 | `RegisterTest` |
| Login | TC_LOG_01 – TC_LOG_04 | `LoginTest` |
| Search | TC_SRC_01 – TC_SRC_05 | `SearchTest` |
| Catalogue | TC_CAT_01 – TC_CAT_07 | `CatalogueTest` |
| Cart | TC_CRT_01 – TC_CRT_06 | `CartTest` |
| Wishlist | TC_WSH_01 – TC_WSH_03 | `WishlistTest` |
| Checkout | TC_CHK_01 – TC_CHK_06 | `CheckoutTest` |

Positive and negative scenarios are covered: valid flows, field validation, wrong credentials,
empty and short search terms, invalid quantities, terms of service not accepted, and order totals.

## Project structure

```
src/test/java
├── configutility     reads GeneralConfiguration.xml (base URL, browser, headless, wait time)
├── helpermethods     Selenium actions wrapped with explicit waits; unique test data
├── logger            Log4j wrapper: one log file per test
├── objectdata        test data objects, filled from the XML files
├── pages             page objects (one class per page or page component)
├── shareddata        TestBasePage: new browser before each test, screenshot on failure
│   └── browser       browser factory: Chrome, Edge, Firefox
├── tests             test classes (one per module)
└── xmlreader         loads the XML test data into objects
src/test/resources
├── GeneralConfiguration.xml
├── testData/         one XML file of test data per module
├── log4j2.xml
└── chaintest.properties
Suites/
├── SmokeSuite.xml        8 key tests (TestNG group "smoke")
└── RegressionSuite.xml   all 37 tests
docs/TestCases.xlsx       test case specification
```

## Design choices

- **Page Object Model with a `BasePage` parent.** Every page object gets the driver, the helper
  methods and `PageFactory` initialisation from one place. Components that appear on several
  pages (`HeaderPage`, `NotificationBar`) have their own classes.
- **Pages act, tests assert.** Page objects return values; all assertions are in the test classes.
- **No `Thread.sleep`.** Every action waits explicitly for its element. Background (AJAX) loading
  in the one-page checkout is detected through the shop's own loading flag.
- **Independent tests.** Each test opens a new browser and creates the data it needs: a new
  account with a generated `@example.com` email, a new cart. No test depends on another or on an
  existing account.
- **Test data outside the code.** Inputs and expected messages are in XML files, so a changed
  text on the site is fixed in the data, not in the tests.
- **Locators chosen for stability.** `id` where the site has one, then CSS classes; XPath is used
  where an element has to be found by its text (a category, a product row in the cart).
- **Parallel execution.** Test classes run in parallel (two threads), each with its own browser.

## Running the tests

Requirements: JDK 25 or newer, Maven, and Chrome (or Edge / Firefox). Selenium Manager downloads
the browser driver automatically.

**From IntelliJ IDEA:** right-click `Suites/RegressionSuite.xml` (or `SmokeSuite.xml`) and choose
**Run**. A single test class or method can be run from its green arrow.

**From the command line:**

```bash
mvn test                                        # regression suite, Chrome, visible browser
mvn test -DsuiteFile=Suites/SmokeSuite.xml      # smoke suite
mvn test -Dbrowser=edge                         # chrome | edge | firefox
mvn test -Dheadless=true                        # no browser window
```

The defaults are set in `src/test/resources/GeneralConfiguration.xml`; the `-D` options override them.

## Results

After a run, everything is in the `target` folder:

| What | Where |
|---|---|
| HTML report (screenshots of failed tests attached) | `target/chaintest/Index.html` |
| Screenshots of failed tests | `target/screenshots/<TestClass>.<testMethod>.png` |
| Log of each test | `target/logs/suite/<TestClass>.<testMethod>.log` |
| All logs in one file | `target/logs/RegressionLogs.log` |

## Continuous integration

`.github/workflows/tests.yml` runs on GitHub Actions with headless Chrome:

- every push and pull request runs the **smoke suite**;
- the **regression suite** is started by hand from the Actions tab (*UI tests > Run workflow*).

The report, logs and screenshots of each run can be downloaded from the run page (artifact
`test-results`).

## Notes

- The checkout tests place real orders on the demo shop, paid "Cash On Delivery", with invented
  names and addresses. A full regression run places 3 orders; the smoke suite places 1.
- Demo Web Shop is a public test site, so its content (products, prices, texts) can change.
  The tests read prices from the page instead of hard-coding them, to keep that risk low.
