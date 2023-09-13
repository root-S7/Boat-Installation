package ren.icraft.boat.installer.operate;

import android.app.Activity;
import android.widget.*;
import java.util.concurrent.Callable;
import ren.icraft.boat.installer.R;
import ren.icraft.boat.installer.tools.FileUtils;
import ren.icraft.boat.installer.tools.IntallAPK;

/**
 * @author Administrator
**/
public class InstallAndDelete implements Callable<String>{
    private Activity activity;
    private FilesPath filesPath;
    private boolean isInstall;
    private TextView textView1,textView2;
    private ProgressBar progressBar1;
    public InstallAndDelete(Activity activity,FilesPath filesPath,boolean isInstall) {
        this.activity = activity;
        this.filesPath = filesPath;
        this.isInstall = isInstall;
        initComponent();
    }
    private void initComponent(){
        this.textView1 = getActivity().findViewById(R.id.loading_text);
        this.textView2 = getActivity().findViewById(R.id.loading_value);
        this.progressBar1 = getActivity().findViewById(R.id.loading_progress_bar);
    }
    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }
    public FilesPath getFilesPath() {
        return filesPath;
    }
    public void setFilesPath(FilesPath filesPath) {
        this.filesPath = filesPath;
    }
    public boolean isInstall() {
        return isInstall;
    }
    public void setInstall(boolean install) {
        isInstall = install;
    }
    @Override
    public String call() {
        activity.runOnUiThread(() -> {
            Toast.makeText(activity.getApplicationContext(), R.string.wait_delete, Toast.LENGTH_SHORT).show();
        });
        FileUtils.deleteFolder(filesPath.getDataDirectory(),true);
        activity.findViewById(R.id.loading_text);
        if (isInstall) {
            activity.runOnUiThread(() -> {
                Toast.makeText(activity.getApplicationContext(), R.string.wait_install_resources, Toast.LENGTH_SHORT).show();
            });
            FileUtils.copyAssetsFilesToPhone(activity.getApplicationContext(), ".minecraft", filesPath.getMinecraftDirectory());
            activity.runOnUiThread(() -> {
                Toast.makeText(activity.getApplicationContext(), R.string.wait_install_apk, Toast.LENGTH_LONG).show();
            });
            IntallAPK.install(filesPath.getApkDirectory(), activity.getApplicationContext());
        }
        /**
         * 以下思路：
         *   1.使用进度条，当进度条走到100%将....
        **/
        return null;
    }
}