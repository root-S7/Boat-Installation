package ren.icraft.boat.installer.tools;

import android.view.View;
import java.util.Map;

public class ViewSwitch{
    //隐藏的布局
    public static void hideLayoutViews(View... views){
        for(View view : views){
            view.setVisibility(View.INVISIBLE);
        }
    }
    //显示的布局
    public static void displayLayoutViews(View... views){
        for(View view : views){
            view.setVisibility(View.VISIBLE);
        }
    }
    public static void changeLayoutView(Map<String,String> map){}
}