package ren.icraft.boat.installer.tools;

import android.widget.TextView;
import java.io.*;
import java.net.*;
import ren.icraft.boat.installer.MainActivity;

public class OnlineAnnouncement extends Thread{
    private MainActivity mainActivity;
    private TextView textView;
    private String allStr = "",URL;
    public OnlineAnnouncement() {}
    public OnlineAnnouncement(TextView textView, MainActivity mainActivity,String URL){
        this.textView = textView;
        this.mainActivity = mainActivity;
        this.URL = URL;
    }
    @Override
    public void run(){
        String url = URL;
        String inputLine = null;
        try {
            URL oracle = new URL(url);
            URLConnection conn = oracle.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while((inputLine = br.readLine()) != null){
                allStr = allStr + inputLine + "\n";
            }
        }catch (IOException e) {
            allStr = "网络异常[若长期看见此消息说明服务器挂了]\n" + e;
        }
        mainActivity.runOnUiThread(() ->{
            textView.setText(allStr);
        });
    }
}
