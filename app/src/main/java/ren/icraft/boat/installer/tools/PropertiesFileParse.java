package ren.icraft.boat.installer.tools;

import android.content.Context;
import java.io.*;
import java.util.Properties;

public class PropertiesFileParse {
    private Properties properties;
    public PropertiesFileParse(String propertiesFileName, Context context) {
        properties = new Properties();
        try {
            InputStream in = context.getAssets().open(propertiesFileName);
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Properties getProperties() {
        return properties;
    }
}