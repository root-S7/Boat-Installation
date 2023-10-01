package ren.icraft.boat.installer.tools;

import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;

public class NormalPermissionRequest{
    public static boolean isAllPermission(Activity activity, String[] permissionList){
        for (String S : permissionList) {
            if(ContextCompat.checkSelfPermission(activity,S) == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }
    //仅限安卓6.0及以上使用3..0
    public static boolean isAlwaysNoPermission(Activity activity, String[] permissionsList){
        for(String S : permissionsList){
            if(!(activity.shouldShowRequestPermissionRationale(S))){
                return true;
            }
        }
        return false;
    }
    public static String[] noPermissionList(Activity activity, String[] permissionsList){
        List<String> temp = new ArrayList<>();
        for(String S : permissionsList){
            if(ContextCompat.checkSelfPermission(activity,S) == PackageManager.PERMISSION_DENIED){
                temp.add(S);
            }
        }
        return temp.toArray(new String[0]);
    }
    public static void showAlertDialogPermission(Activity activity, String[] permissionList, int requestCode){
        AlertDialog.Builder alertDialogPerMission = new AlertDialog.Builder(activity);
        alertDialogPerMission.setCancelable(false);
        alertDialogPerMission.setTitle("检测到无权限");
        alertDialogPerMission.setMessage("请授予应用必要权限否则无法使用！");
        alertDialogPerMission.setPositiveButton("授予权限", (dialog, which) -> {
            if(isAlwaysNoPermission(activity, noPermissionList(activity,permissionList))){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", activity.getPackageName(),null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }else{
                ActivityCompat.requestPermissions(activity, permissionList, requestCode);
            }
        });
        alertDialogPerMission.setNegativeButton("关闭", (dialog, which) -> dialog.cancel());
        alertDialogPerMission.show();
    }
}