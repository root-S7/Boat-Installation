package ren.icraft.boat.installer.operate;

import android.content.Context;
import android.os.Environment;
import ren.icraft.boat.installer.AppApplication;

public class FilesPath{
    private String dataDirectory,minecraftDirectory,apkDirectory;
    public FilesPath(Context context) {
        dataDirectory = Environment.getExternalStorageDirectory() + "/" + AppApplication.properties.getProperty("putDirectory");
        minecraftDirectory = dataDirectory + "/.minecraft";
        apkDirectory = context.getExternalFilesDir(null) + "/" + AppApplication.properties.getProperty("installAPKName");
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