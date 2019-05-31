package com.example.legend.remoteclient.core;

import android.app.Activity;
import android.app.ProgressDialog;

import com.example.legend.common.packet.FileResponsePacket;
import com.example.legend.common.task.BaseMultiThreadDownloadTask;

/**
 * @author Legend
 * @data by on 19-5-29.
 * @description
 */
public class MultiFileDownloadTask extends BaseMultiThreadDownloadTask {

    private Activity mContext;
    private ProgressDialog mProgressDialog;

    public MultiFileDownloadTask(String basePath, FileResponsePacket packet, Activity context) {
        super(basePath, packet);
        this.mContext = context;
    }

    @Override
    public void preExecute() {
        mContext.runOnUiThread(() -> {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle("Downloading File");
            mProgressDialog.setMessage("Please Wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        });
    }

    @Override
    public void updateProgress(int value) {
        mContext.runOnUiThread(() -> mProgressDialog.setProgress(value));
    }

    @Override
    public void postExecute() {
        super.postExecute();
        mContext.runOnUiThread(() -> {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        });
    }
}
