package ren.icraft.boat.installer.operate;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.*;
import java.util.concurrent.Callable;
import ren.icraft.boat.installer.R;
import ren.icraft.boat.installer.tools.FileUtils;
import ren.icraft.boat.installer.tools.IntallAPK;

/**
 * @author Administrator
**/
public class InstallAndDelete extends AsyncTask<Void, Void, Void> {
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
    public TextView getTextView1() {
        return textView1;
    }
    public void setTextView1(TextView textView1) {
        this.textView1 = textView1;
    }
    public TextView getTextView2() {
        return textView2;
    }
    public void setTextView2(TextView textView2) {
        this.textView2 = textView2;
    }
    public ProgressBar getProgressBar1() {
        return progressBar1;
    }
    public void setProgressBar1(ProgressBar progressBar1) {
        this.progressBar1 = progressBar1;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), R.string.wait_delete, Toast.LENGTH_SHORT).show());
        FileUtils.deleteFolder(filesPath.getDataDirectory(),this);
        activity.findViewById(R.id.loading_text);
        if (isInstall) {
            activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), R.string.wait_install_resources, Toast.LENGTH_SHORT).show());
            FileUtils.copyAssetsFilesToPhone(activity.getApplicationContext(), ".minecraft", filesPath.getMinecraftDirectory(),this);
            activity.runOnUiThread(() -> Toast.makeText(activity.getApplicationContext(), R.string.wait_install_apk, Toast.LENGTH_LONG).show());
            IntallAPK.install(filesPath.getApkDirectory(), activity.getApplicationContext());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        Handler handler = new Handler();
        handler.postDelayed(() -> activity.finish(), 1000);
    }
}