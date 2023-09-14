package ren.icraft.boat.installer;

import android.app.Application;
import java.util.Properties;
import ren.icraft.boat.installer.operate.FilesPath;
import ren.icraft.boat.installer.tools.PropertiesFileParse;

/**
 * @author Administrator
**/
public class AppApplication extends Application{
    public static Properties properties;
    public static FilesPath filesPath;
    @Override
    public void onCreate() {
        properties = new PropertiesFileParse("config.properties", getApplicationContext()).getProperties();
        filesPath = new FilesPath(getApplicationContext());
        super.onCreate();
    }
}