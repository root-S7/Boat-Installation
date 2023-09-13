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
import java.io.Serializable;
import ren.icraft.boat.installer.tools.*;

/**
 * @author Administrator
**/
public class MainActivity extends AppCompatActivity implements View.OnClickListener,Serializable{
    private String[] legacyAccessFilesPermission = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
    }
    private void initComponent(){
        findViewById(R.id.delete_resource).setOnClickListener(this);
        findViewById(R.id.install_resource).setOnClickListener(this);
        findViewById(R.id.qq_group).setOnClickListener(this);
        if("true".equals(AppApplication.properties.getProperty("enableAnnouncement"))){
            new OnlineAnnouncement(findViewById(R.id.network_announcement),MainActivity.this,AppApplication.properties.getProperty("AnnouncementURL")).start();
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
        boolean isInstall = false;
        switch(v.getId()){
            case R.id.install_resource:
                if((Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) && !(getPackageManager().canRequestPackageInstalls())){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    intent.setData(Uri.fromParts("package", getPackageName(),null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                }
                isInstall = true;
            case R.id.delete_resource:
                Intent intent = new Intent(this,WaitActivity.class);

                intent.putExtra("isInstall",isInstall);

                startActivity(intent);
                break;
            case R.id.qq_group:
                QQGroup.joinQQGroup(getApplicationContext(),AppApplication.properties.getProperty("QQGroupKey"));
                break;
            default:
                Log.d("点击事件", "未能匹配有效按钮！");
                break;
        }
    }

    //用于记录返回键按下时间
    private long mPressedTime = 0;
    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();
        if ((mNowTime - mPressedTime) > 2000) {
            Toast.makeText(this, "再按一次返回键退出应用程序", Toast.LENGTH_SHORT).show();
            mPressedTime = mNowTime;
        } else {
            this.finish();
            System.exit(0);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((requestCode == 1000) && !(NormalPermissionRequest.isAllPermission(this,legacyAccessFilesPermission))){
            NormalPermissionRequest.showAlertDialogPermission(MainActivity.this, legacyAccessFilesPermission, 1000);
        }
    }
}