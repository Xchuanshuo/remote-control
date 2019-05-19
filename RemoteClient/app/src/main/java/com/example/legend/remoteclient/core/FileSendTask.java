package com.example.legend.remoteclient.core;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.legend.common.core.TaskHandleCenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-14.
 * @description
 */
public class FileSendTask extends AsyncTask<String, String, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private OutputStream outputStream;
    private ProgressDialog mProgressDialog;
    /** true表示本地主动上传 false表示远程请求下载 **/
    private boolean flag;

    public FileSendTask(Context context, boolean flag) throws IOException {
        this.mContext = context;
        this.flag = flag;
    }

    public FileSendTask(Context context) throws IOException {
        this(context, true);
    }

    @Override
    protected void onPreExecute() {
        if (flag) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle("Downloading File");
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();
        Socket socket = handleCenter.getSocket();
        try {
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File(strings[0]);
        long fileSize = Long.parseLong(strings[1]);
        if (flag) {
            mProgressDialog.setMessage(file.getName());
        }
        FileInputStream fis = null;
        byte[] buffer = new byte[1024 * 1024];
        int remaining = (int)fileSize, read = 0;
        long totalWritten = 0;
        try {
            fis = new FileInputStream(file);
            while (totalWritten < fileSize && (read = fis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                totalWritten += read;
                remaining -= read;
                Log.d(FileSendTask.class.getName(), totalWritten +" : " + fileSize);
                publishProgress(String.valueOf((int)(((double)totalWritten/fileSize)*100)));
                outputStream.write(buffer, 0, read);
            }
            fis.close();
            outputStream.flush();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onProgressUpdate(String...values) {
        if (flag) {
            Log.d(FileSendTask.class.getName(), values[0]);
            mProgressDialog.setProgress(Integer.parseInt(values[0]));
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (flag && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
