package context;

import fontend.DemoDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Context state is responsible for loading properties and storing any information that need to be access across
 */

public class ContextState {
    private static final Properties prop = new Properties();
    private static boolean isLoggedin = false;

    public static void loadProperties() {
        try (InputStream input = ContextState.class.getClassLoader().getResourceAsStream("application.properties")) {
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static boolean isIsLoggedin() {
        return isLoggedin;
    }

    public static void setIsLoggedin(boolean isLoggedin) {
        ContextState.isLoggedin = isLoggedin;
    }
}
