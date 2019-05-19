package com.example.legend.remoteclient;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.util.Log;

import com.example.legend.remoteclient.control.BaseControlActivity;
import com.example.legend.remoteclient.control.FileDownloadActivity;
import com.example.legend.remoteclient.control.FileUploadActivity;
import com.example.legend.remoteclient.control.KeyBoardControlActivity;
import com.example.legend.remoteclient.control.MouseControlActivity;
import com.example.legend.remoteclient.control.PowerControlActivity;

/**
 * @author legend
 */

public class HomeActivity extends BaseControlActivity {

    private CardView mMouseControl;
    private CardView mPowerControl;
    private CardView mKeyBoardControl;
    private CardView mDownloadControl;
    private CardView mFileUploadControl;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    public int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void initData() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    protected void initView() {
        mMouseControl = findViewById(R.id.card_mouse);
        mPowerControl = findViewById(R.id.card_power);
        mKeyBoardControl = findViewById(R.id.card_key_board);
        mDownloadControl = findViewById(R.id.card_download);
        mFileUploadControl = findViewById(R.id.card_file_upload);
    }

    @Override
    protected void initListener() {
        mMouseControl.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MouseControlActivity.class)));
        mPowerControl.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PowerControlActivity.class)));
        mKeyBoardControl.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, KeyBoardControlActivity.class)));
        mDownloadControl.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FileDownloadActivity.class)));
        mFileUploadControl.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, FileUploadActivity.class)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
}
