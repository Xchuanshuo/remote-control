package com.example.legend.remoteclient.core;

import android.os.Environment;

import java.io.IOException;

/**
 * @author Legend
 * @data by on 19-5-16.
 * @description
 */
public class FileApi {

    public static String homeDirectoryPath = "";

    public static String getExternalStoragePath() {
        String rootPath = System.getenv("EXTERNAL_STORAGE");
        try {
            rootPath = Environment.getExternalStorageDirectory().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootPath;
    }

}
