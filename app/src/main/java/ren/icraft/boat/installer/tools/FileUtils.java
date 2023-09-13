package ren.icraft.boat.installer.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import ren.icraft.boat.installer.operate.InstallAndDelete;

public class FileUtils {
    public static void copyAssetsFilesToPhone_NoProgressBar(Context context, String assetsPath, String savePath){
        try {
            // 获取assets目录下的所有文件及目录名
            String[] fileNames = context.getAssets().list(assetsPath);
            // 如果是目录
            if (fileNames.length > 0) {
                File file = new File(savePath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyAssetsFilesToPhone_NoProgressBar(context, assetsPath + "/" + fileName, savePath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(assetsPath);
                FileOutputStream fos = new FileOutputStream(savePath);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                // 循环从输入流读取
                while ((byteCount = is.read(buffer)) != -1) {
                    // 将读取的输入流写入到输出流
                    fos.write(buffer, 0, byteCount);
                }
                // 刷新缓冲区
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //单位均为Byte
    private static long deleteFileSizeCount = 0;
    private static long pathSize;
    private static double nowProgressShare;
    public static void copyAssetsFilesToPhone(Context context, String assetsPath, String savePath, InstallAndDelete installAndDelete){
        pathSize = getAssetsFolderSize(installAndDelete.getActivity(),".minecraft");
        if(pathSize > 0){
            installAndDelete.getProgressBar1().setMax(100);
        }
        Log.d("事件", String.valueOf(pathSize));
        deleteFileSizeCount = 0;
        nowProgressShare = 0;
        startCopyAssetsFilesToPhone(context,assetsPath,savePath,installAndDelete);
    }
    private static void startCopyAssetsFilesToPhone(Context context, String assetsPath, String savePath, InstallAndDelete installAndDelete){
        try {
            // 获取assets目录下的所有文件及目录名
            String[] fileNames = context.getAssets().list(assetsPath);
            // 如果是目录
            if (fileNames.length > 0) {
                File file = new File(savePath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    startCopyAssetsFilesToPhone(context, assetsPath + "/" + fileName, savePath + "/" + fileName, installAndDelete);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(assetsPath);
                FileOutputStream fos = new FileOutputStream(savePath);
                byte[] buffer = new byte[8192];
                int byteCount = 0;
                // 循环从输入流读取
                while ((byteCount = is.read(buffer)) != -1) {
                    // 将读取的输入流写入到输出流
                    fos.write(buffer, 0, byteCount);

                    deleteFileSizeCount += byteCount;
                }
                installAndDelete.getActivity().runOnUiThread(() -> {
                    nowProgressShare = (((double)deleteFileSizeCount / pathSize) * 100);
                    installAndDelete.getProgressBar1().setProgress((int)nowProgressShare);
                    installAndDelete.getTextView2().setText(String.format("%.2f",nowProgressShare) + " %");
                    //Log.d("事件5", "当前文件大小" + thisFileSize + "Byte，已累加大小" + deleteFileSizeCount + "Byte，需要删除的文件总大小" + pathSize + "Byte，当前进度删除" + nowProgressShare + " %");
                });
                //Log.d("事件", String.valueOf(deleteFileSizeCount));
                // 刷新缓冲区
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
    **/
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
    /**
     * 删除文件夹以及目录下的文件
     * @param   path 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
    **/
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private static void deleteDirectory(String path, InstallAndDelete installAndDelete){
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }
        //统计path的根目录下有多少个文件夹和文件总和
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                deleteFileSizeCount += getPathSize(files[i].getAbsolutePath());
                //删除文件
                deleteFile(files[i].getAbsolutePath());
                installAndDelete.getActivity().runOnUiThread(() -> {
                    nowProgressShare = (((double)deleteFileSizeCount / pathSize) * 100);
                    installAndDelete.getProgressBar1().setProgress((int)nowProgressShare);
                    installAndDelete.getTextView2().setText(String.format("%.2f",nowProgressShare) + " %");
                });
            } else {
                //删除目录[递归先删除里面文件最后删空目录]
                deleteDirectory(files[i].getAbsolutePath(),installAndDelete);
            }
        }
        //删除当前空目录
        dirFile.delete();
    }
    public static boolean deleteDirectory_NoProgressBar(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {break;}
            } else {
                //删除子目录
                flag = deleteDirectory_NoProgressBar(files[i].getAbsolutePath());
                if (!flag) {break;}
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }
    /**
     * 根据路径删除指定的目录或文件，无论存在与否
     * @param filePath  要删除的目录或文件
     * @return 删除成功返回 true，否则返回 false。
    **/
    public static boolean deleteFolder(String filePath,InstallAndDelete installAndDelete) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                if(installAndDelete != null) {
                    pathSize = getPathSize(filePath);
                    if(pathSize > 0){
                        installAndDelete.getProgressBar1().setMax(100);
                    }
                    deleteFileSizeCount = 0;
                    nowProgressShare = 0;
                    deleteDirectory(filePath,installAndDelete);
                    return new File(filePath).exists();
                }
                else return deleteDirectory_NoProgressBar(filePath);
            }
        }
    }

    /**
     * 多线程统计目录大小(适合特别多小文件使用)
     * 该方法仅适合有权限的“公有，私有目录”
    **/
    public static long getPathSize(String dir) {
        File path = new File(dir);
        //如果path不是目录则输出该文件大小
        if(!path.isDirectory()) {
            return path.length();
        }
        //是目录则开始多线程统计大小
        return IoOperateHolder.FORKJOIN_POOL.invoke(new CalDirCommand(path));
    }
    static class CalDirCommand extends RecursiveTask<Long> {
        private File folder;
        CalDirCommand(File folder){
            this.folder = folder;
        }
        @Override
        protected Long compute() {
            AtomicLong size = new AtomicLong(0);
            File[] files = folder.listFiles();
            if(files == null || files.length == 0) {
                return 0L;
            }
            List<ForkJoinTask<Long>> jobs = new ArrayList<>();
            for(File f : files) {
                if(!f.isDirectory()) {
                    size.addAndGet(f.length());
                } else {
                    jobs.add(new CalDirCommand(f));
                }
            }
            for(ForkJoinTask<Long> t : invokeAll(jobs)) {
                size.addAndGet(t.join());
            }
            return size.get();
        }
    }
    private static final class IoOperateHolder {
        final static ForkJoinPool FORKJOIN_POOL = new ForkJoinPool();
    }
    /**
     * 单线程统计当前应用的assets目录种指定目录大小
     * 该方法不推荐用于计算小文件较多目录否则可能会崩溃
    **/
    public static long getAssetsFolderSize(Context context, String srcPath){
        String[] fileNames;
        try {
            fileNames = context.getAssets().list(srcPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        long size = 0;
        if (fileNames.length > 0) {
            for (String fileName : fileNames) {
                //assets文件夹下的目录
                if (!srcPath.equals("")) {
                    size += getAssetsFolderSize(context, srcPath + File.separator + fileName);
                    //assets 文件夹
                } else {
                    size += getAssetsFolderSize(context, fileName);
                }
            }
        } else {
            try {
                InputStream is = context.getAssets().open(srcPath);
                size += is.available();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return size;
    }

}