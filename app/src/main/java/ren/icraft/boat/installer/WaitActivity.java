package ren.icraft.boat.installer;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.concurrent.FutureTask;
import ren.icraft.boat.installer.operate.FilesPath;
import ren.icraft.boat.installer.operate.InstallAndDelete;

/**
 * @author Administrator
**/
public class WaitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        //finish();

        FutureTask<String> stringFutureTask = new FutureTask<>(new InstallAndDelete(this,AppApplication.filesPath,getIntent().getBooleanExtra("isInstall", false)));
        Thread thread = new Thread(stringFutureTask);
        thread.start();
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
}