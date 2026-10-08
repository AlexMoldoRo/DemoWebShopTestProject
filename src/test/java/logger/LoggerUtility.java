package logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Writes one log file per test (target/logs/suite/) and, at the end of the run,
 * merges all of them into target/logs/RegressionLogs.log.
 */
public class LoggerUtility {

    private static final String SUITE_LOGS_PATH = "target/logs/suite/";
    private static final String REGRESSION_LOGS_PATH = "target/logs/";

    private static final Logger logger = LogManager.getLogger();

    public static synchronized void startTestCase(String testName) {
        // log4j2.xml uses "threadName" to decide in which file the lines of this thread are written
        ThreadContext.put("threadName", testName);
        logger.info("===== Execution started: " + testName + " =====");
    }

    public static synchronized void endTestCase(String testName) {
        logger.info("===== Execution ended: " + testName + " =====");
    }

    public static synchronized void infoLog(String message) {
        logger.info(getCallInfo() + " " + message);
    }

    public static synchronized void errorLog(String message) {
        logger.error(getCallInfo() + " " + message);
    }

    // Finds the class and method that asked for the log line
    private static String getCallInfo() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        return caller.getClassName() + " : " + caller.getMethodName() + " =>";
    }

    public static void mergeFiles() {
        File dir = new File(SUITE_LOGS_PATH);
        String[] fileNames = dir.list();
        if (fileNames == null) {
            return; // nothing was logged
        }
        Arrays.sort(fileNames);

        try (PrintWriter writer = new PrintWriter(REGRESSION_LOGS_PATH + "RegressionLogs.log")) {
            for (String fileName : fileNames) {
                writer.println("Content of " + fileName);
                try (BufferedReader reader = new BufferedReader(new FileReader(new File(dir, fileName)))) {
                    String line = reader.readLine();
                    while (line != null) {
                        writer.println(line);
                        line = reader.readLine();
                    }
                }
                writer.println();
            }
        } catch (IOException e) {
            logger.error("Could not merge the log files: " + e.getMessage());
        }
    }
}
