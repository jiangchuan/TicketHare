package io.chizi.tickethare.util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.chizi.tickethare.MainActivity;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public class FileUtil {

    private static final String LOG_TAG = FileUtil.class.getName();

    public static File createImageFile(Activity activity, String imageFileName, String suffix) throws IOException {
        File storageDir = getStorageDir(activity);
//        File imageF = File.createTempFile(imageFileName, suffix, storageDir);
//        File imageF = new File(storageDir + "/" + imageFileName + suffix);
        File imageF = new File(storageDir, imageFileName + suffix);
        return imageF;
    }

    public static String writeByteStringToFile(Activity activity, String imageFileName, String suffix, ByteString mByteString) {
        byte[] mByte = new byte[mByteString.size()];
        mByteString.copyTo(mByte, 0);
        String theFilePath = getStorageDir(activity) + "/" + imageFileName + suffix;
        try {
            writeFile(mByte, theFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return theFilePath;
    }

    public static void writeFile(byte[] data, String fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName);
        out.write(data);
        out.close();
    }

    public static File getStorageDir(Activity activity) {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = activity.getExternalFilesDir(null);
        } else {
            Log.v(LOG_TAG, "External storage is not mounted READ/WRITE.");
        }
        return storageDir;
    }

    public static boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }


    public static String getFileName(String ticketIDStr, String filePrefix) {
        return (ticketIDStr + filePrefix);
    }

    public static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            new File(toPath).mkdirs();
            boolean res = true;
            for (String file : files)
                if (file.contains("."))
                    res &= copyAsset(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                else
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


}
