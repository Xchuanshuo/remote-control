package com.example.legend.remoteclient.core;

import android.app.Activity;
import android.app.ProgressDialog;

import com.example.legend.common.packet.FileDownloadRequestPacket;
import com.example.legend.common.task.BaseFileSendTask;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Legend
 * @data by on 19-5-14.
 * @description
 */
public class FileSendTask extends BaseFileSendTask {

    private Activity mContext;
    private ProgressDialog mProgressDialog;
    /** true表示本地主动上传 false表示远程请求下载 **/
    private boolean flag;

    public FileSendTask(FileDownloadRequestPacket packet, Socket socket,
                        Activity context, boolean flag) throws IOException {
        super(packet, socket);
        this.mContext = context;
        this.flag = flag;
    }

    public FileSendTask(FileDownloadRequestPacket packet, Socket socket,
                        Activity context) throws IOException {
        this(packet, socket, context, true);
    }

    @Override
    public void preExecute() {
        mContext.runOnUiThread(() -> {
            if (flag) {
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.setTitle("Uploading File");
                mProgressDialog.setMessage("Please Wait...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setProgress(0);
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void postExecute() {
        super.postExecute();
        mContext.runOnUiThread(() -> {
            if (mProgressDialog != null &&  flag && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        });
    }

}
