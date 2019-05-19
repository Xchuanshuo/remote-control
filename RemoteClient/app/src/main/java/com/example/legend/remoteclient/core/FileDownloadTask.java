package com.example.legend.remoteclient.core;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.example.legend.common.core.TaskHandleCenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-12.
 * @description
 */
public class FileDownloadTask extends AsyncTask<String,String, Void> {

    TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity mContext;
    private InputStream inputStream;
    private ProgressDialog mProgressDialog;

    public FileDownloadTask(AppCompatActivity context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Downloading File");
        mProgressDialog.setMessage("Please Wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(0);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(String...params) {
        Socket socket = handleCenter.getSocket();
        try {
            this.inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = params[0];
        mContext.runOnUiThread(() -> mProgressDialog.setMessage(name));
        long fileSize = Long.parseLong(params[1]);
        FileOutputStream fos = null;
        String path = FileApi.getExternalStoragePath() +"/RemoteControl/" + name;
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        int read = 0, remaining = (int) fileSize;
        long totalRead = 0;
        byte[] buffer = new byte[1024 * 1024];
        try {
            fos = new FileOutputStream(file);
            while ((read = inputStream.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalRead += read;
                remaining -= read;
                publishProgress(String.valueOf((int)(((double)totalRead/fileSize)*100)));
                fos.write(buffer, 0, read);
            }
            fos.flush();
            fos.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String...values) {
        mProgressDialog.setProgress(Integer.parseInt(values[0]));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
