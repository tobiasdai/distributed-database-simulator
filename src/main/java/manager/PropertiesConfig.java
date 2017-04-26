package manager;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by dais on 2017-4-26.
 */
public class PropertiesConfig {
    public static Properties properties = getPropertiesConfig("config.properties");

    public static String readData(String key) {
        String value = properties.getProperty(key);
        return value;
    }

    public static Properties getPropertiesConfig(String filePath) {
        filePath = PropertiesConfig.class.getResource("/" + filePath).getPath();
        Properties props = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            props.load(in);
            in.close();
            return props;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
