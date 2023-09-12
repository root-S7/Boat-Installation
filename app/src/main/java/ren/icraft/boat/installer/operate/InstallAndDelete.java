package ren.icraft.boat.installer.operate;

import android.app.Activity;
import android.widget.Toast;
import java.io.Serializable;
import java.util.concurrent.Callable;
import ren.icraft.boat.installer.R;
import ren.icraft.boat.installer.tools.AssetsToStorageDir;
import ren.icraft.boat.installer.tools.DeleteResources;
import ren.icraft.boat.installer.tools.IntallAPK;

/**
 * @author Administrator
**/
public class InstallAndDelete implements Callable<String>, Serializable {
    Activity activity;
    FilesPath filesPath;
    boolean isInstall;
    public InstallAndDelete(Activity activity,FilesPath filesPath,boolean isInstall) {
        this.activity = activity;
        this.filesPath = filesPath;
        this.isInstall = isInstall;
    }
    @Override
    public String call() {
        activity.runOnUiThread(() -> {
            Toast.makeText(activity.getApplicationContext(), R.string.wait_delete, Toast.LENGTH_SHORT).show();
        });
        DeleteResources.deleteFolder(filesPath.getDataDirectory());
        if (isInstall) {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity.getApplicationContext(), R.string.wait_install_resources, Toast.LENGTH_SHORT).show();
            });
            AssetsToStorageDir.copyFilesFromAssets(activity.getApplicationContext(), ".minecraft", filesPath.getMinecraftDirectory());
            activity.runOnUiThread(() -> {
                Toast.makeText(activity.getApplicationContext(), R.string.wait_install_apk, Toast.LENGTH_LONG).show();
            });
            IntallAPK.install(filesPath.getApkDirectory(), activity.getApplicationContext());
        }
        return null;
    }
}