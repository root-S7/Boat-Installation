package ren.icraft.boat.installer;

import android.app.Application;

import java.util.Properties;

import ren.icraft.boat.installer.tools.PropertiesFileParse;

public class AppApplication extends Application{
    public static Properties properties;
    @Override
    public void onCreate() {
        properties = new PropertiesFileParse("config.properties", getApplicationContext()).getProperties();
        super.onCreate();
    }
}