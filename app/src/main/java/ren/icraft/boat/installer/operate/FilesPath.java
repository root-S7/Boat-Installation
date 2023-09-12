package ren.icraft.boat.installer.operate;

import android.os.Environment;

import ren.icraft.boat.installer.AppApplication;

public class FilesPath {
    private String dataDirectory,minecraftDirectory,apkDirectory;
    public FilesPath() {
        dataDirectory = Environment.getExternalStorageDirectory() + "/" + AppApplication.properties.getProperty("putDirectory");
        minecraftDirectory = dataDirectory + "/.minecraft";
        apkDirectory = minecraftDirectory + AppApplication.properties.getProperty("installAPKName");
    }
    public String getDataDirectory() {
        return dataDirectory;
    }
    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }
    public String getMinecraftDirectory() {
        return minecraftDirectory;
    }
    public void setMinecraftDirectory(String minecraftDirectory) {
        this.minecraftDirectory = minecraftDirectory;
    }
    public String getApkDirectory() {
        return apkDirectory;
    }
    public void setApkDirectory(String apkDirectory) {
        this.apkDirectory = apkDirectory;
    }
}