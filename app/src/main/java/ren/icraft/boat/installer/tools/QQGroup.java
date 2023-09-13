package ren.icraft.boat.installer.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class QQGroup{
    public final static String QQ_GROUP_API = "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D";
    public static void joinQQGroup(Context context,String QQGroupKey){
        Intent intent = new Intent();
        intent.setData(Uri.parse(QQ_GROUP_API + QQGroupKey));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
        }
    }
}