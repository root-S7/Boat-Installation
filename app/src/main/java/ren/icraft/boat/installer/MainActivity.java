package ren.icraft.boat.installer;

import android.Manifest;
import android.annotation.*;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.util.Properties;
import ren.icraft.boat.installer.tools.*;

/**
 * @author Administrator
**/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout linearLayout1,linearLayout2;
    private RelativeLayout relativeLayout1,relativeLayout2;
    private String[] legacyAccessFilesPermission = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Properties properties;
    private String putDirectory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init(){
        properties = new PropertiesFileParse("config.properties", getApplicationContext()).getProperties();
        putDirectory = properties.getProperty("putDirectory");
        linearLayout1 = findViewById(R.id.app_1);
        relativeLayout1 = findViewById(R.id.app_3);
        relativeLayout2 = findViewById(R.id.app_4);
        linearLayout2 = findViewById(R.id.app_2);
        findViewById(R.id.delete_resource).setOnClickListener(this);
        findViewById(R.id.install_resource).setOnClickListener(this);
        findViewById(R.id.qq_group).setOnClickListener(this);
        if("true".equals(properties.getProperty("enableAnnouncement"))){
            new OnlineAnnouncement(findViewById(R.id.network_announcement),MainActivity.this,properties.getProperty("AnnouncementURL")).start();
        }
    }
    @Override
    public void onClick(View v) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if(Environment.isExternalStorageManager()){
                onClickNextStep(v);
            }else{
                AlertDialog.Builder alertDialogAccessFiles = new AlertDialog.Builder(this);
                alertDialogAccessFiles.setCancelable(false);
                alertDialogAccessFiles.setMessage("请授予读写权限，否则应用无法正常运行");
                alertDialogAccessFiles.setPositiveButton("给予权限", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1);
                });
                alertDialogAccessFiles.setNegativeButton("取消(不给予)", null);
                alertDialogAccessFiles.create();
                alertDialogAccessFiles.show();
            }
        }else{
            if(NormalPermissionRequest.isAllPermission(this,legacyAccessFilesPermission)){
                onClickNextStep(v);
            }else{
                ActivityCompat.requestPermissions(this, legacyAccessFilesPermission, 1000);
            }
        }
    }
    @SuppressLint("NonConstantResourceId")
    private void onClickNextStep(View v){
        switch(v.getId()){
            case R.id.delete_resource:
                if(new File(Environment.getExternalStorageDirectory() + "/" + putDirectory).exists()){
                    doSome(false);
                }
                break;
            case R.id.install_resource:
                if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && !(getPackageManager().canRequestPackageInstalls())){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    intent.setData(Uri.fromParts("package", getPackageName(),null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }
                doSome(true);
                break;
            case R.id.qq_group:
                QQGroup.joinQQGroup(getApplicationContext(),properties.getProperty("QQGroupKey"));
                break;
            default:
                Log.d("点击事件", "未能匹配有效按钮！");
                break;
        }
    }
    private void doSome(boolean writeAssetsToStorageDir){
        Toast.makeText(getApplicationContext(), R.string.wait_delete, Toast.LENGTH_SHORT).show();
        ViewSwitch.hideLayoutViews(linearLayout1,relativeLayout1,relativeLayout2);
        ViewSwitch.displayLayoutViews(linearLayout2);
        new Thread(()->{
            DeleteResources.deleteFolder(Environment.getExternalStorageDirectory() + "/" + putDirectory);
            if(writeAssetsToStorageDir){
                MainActivity.this.runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(),R.string.wait_install_resources,Toast.LENGTH_LONG).show();
                });
                AssetsToStorageDir.copyFilesFromAssets(getApplicationContext(),".minecraft",Environment.getExternalStorageDirectory() + "/" + putDirectory + "/.minecraft");
                MainActivity.this.runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(),R.string.wait_install_apk,Toast.LENGTH_LONG).show();
                });
                IntallAPK.install(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + putDirectory + "/.minecraft/" + properties.getProperty("installAPKName"),getApplicationContext());
            }
            MainActivity.this.runOnUiThread(() ->{
                ViewSwitch.hideLayoutViews(linearLayout2);
                ViewSwitch.displayLayoutViews(linearLayout1,relativeLayout1,relativeLayout2);
            });
        }).start();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((requestCode == 1000) && !(NormalPermissionRequest.isAllPermission(this,legacyAccessFilesPermission))){
            NormalPermissionRequest.showAlertDialogPermission(MainActivity.this, legacyAccessFilesPermission, 1000);
        }
    }
}