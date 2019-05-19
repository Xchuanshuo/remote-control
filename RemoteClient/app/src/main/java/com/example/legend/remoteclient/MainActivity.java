package com.example.legend.remoteclient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.legend.common.Constants;
import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.task.MakeConnection;
import com.example.legend.remoteclient.control.BaseControlActivity;

/**
 * @author legend
 */
public class MainActivity extends BaseControlActivity {

    private final String IP = "ip";
    private final String PORT = "port";
    private EditText mIpEdt;
    private EditText mPortEdt;
    private Button mConnectBtn;
    private long exitTime = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mIpEdt = findViewById(R.id.edt_ip);
        mPortEdt = findViewById(R.id.edt_port);
        mConnectBtn = findViewById(R.id.btn_connect);
    }

    @Override
    protected void initListener() {
        mConnectBtn.setOnClickListener(view -> {
            Constants.TARGET_IP = mIpEdt.getText().toString();
            Constants.TCP_PORT = Integer.parseInt(mPortEdt.getText().toString());
            Toast.makeText(MainActivity.this, "正在连接...", Toast.LENGTH_SHORT).show();
            new MakeConnection(socket -> {
                handleCenter.setSocket(socket);
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                editor.putString(IP, Constants.TARGET_IP);
                editor.putInt(PORT, Constants.TCP_PORT);
                editor.apply();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show());
            });
        });
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void initData() {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.editor = sharedPreferences.edit();
        String ip = sharedPreferences.getString(IP, "");
        int port = sharedPreferences.getInt(PORT, -1);
        mIpEdt.setText(ip);
        mPortEdt.setText(String.valueOf(port));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                TaskHandleCenter.getInstance().exit();
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
