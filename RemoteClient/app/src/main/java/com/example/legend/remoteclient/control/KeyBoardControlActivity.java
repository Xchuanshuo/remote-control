package com.example.legend.remoteclient.control;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.legend.common.Constants;
import com.example.legend.remoteclient.R;

/**
 * @author legend
 */
public class KeyBoardControlActivity extends BaseControlActivity {

    private EditText mInputEdt;
    private Button mSendButton;
    private Button mClearButton;
    private Button mEnterButton;
    private Button mDosButton;
    private Button mUpButton;
    private Button mDownButton;
    private Button mLeftButton;
    private Button mRightButton;

    @Override
    public int getLayout() {
        return R.layout.activity_key_board_control;
    }

    @Override
    protected void initView() {
        mInputEdt = findViewById(R.id.edt_input);
        mSendButton = findViewById(R.id.btn_send);
        mClearButton = findViewById(R.id.btn_clear);
        mEnterButton = findViewById(R.id.btn_enter);
        mDosButton = findViewById(R.id.btn_dos);
        mUpButton = findViewById(R.id.btn_up);
        mDownButton = findViewById(R.id.btn_down);
        mLeftButton = findViewById(R.id.btn_left);
        mRightButton = findViewById(R.id.btn_right);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        mSendButton.setOnClickListener(v -> {
            String s = mInputEdt.getText().toString();
            if (TextUtils.isEmpty(s)) {
                Toast.makeText(KeyBoardControlActivity.this, "信息为空",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage("keyboard:message," + s);
        });
        mClearButton.setOnClickListener(v -> sendMessage("keyboard:key,Back_Space,click"));
        mEnterButton.setOnClickListener(v -> sendMessage("keyboard:key,Enter,click"));
        mDosButton.setOnClickListener(v -> {
            String s = mInputEdt.getText().toString();
            if (TextUtils.isEmpty(s)) {
                Toast.makeText(KeyBoardControlActivity.this, "信息为空",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            sendMessage("keyboard:dos_message," + s);
        });
        mUpButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendMessage("keyboard:key,Up,down");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendMessage("keyboard:key,Up,up");
                sendMessage("keyboard:key,Left,up");
                sendMessage("keyboard:key,Right,up");
            }
            return true;
        });
        mLeftButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendMessage("keyboard:key,Left,down");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendMessage("keyboard:key,Left,up");
                sendMessage("keyboard:key,Up,up");
                sendMessage("keyboard:key,Down,up");
            } else if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
                if (event.getX(1) > 150 && event.getX(1) < 300
                        && event.getY(1) < 0) {
                    sendMessage("keyboard:key,Up,down");
                }
                if (event.getX(1) > 150 && event.getX(1) < 300
                        && event.getY(1) > 0) {
                    sendMessage("keyboard:key,Down,down");
                }
            } else if (event.getAction() == MotionEvent.ACTION_POINTER_2_UP) {
                if (event.getX(1) > 150 && event.getX(1) < 300
                        && event.getY(1) < 0) {
                    sendMessage("keyboard:key,Up,up");
                }
                if (event.getX(1) > 150 && event.getX(1) < 300
                        && event.getY(1) > 0) {
                    sendMessage("keyboard:key,Down,up");
                }
            }
            return true;
        });
        mDownButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendMessage("keyboard:key,Down,down");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendMessage("keyboard:key,Down,up");
                sendMessage("keyboard:key,Left,up");
                sendMessage("keyboard:key,Right,up");
            }
            return true;
        });
        mRightButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                sendMessage("keyboard:key,Right,down");
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                sendMessage("keyboard:key,Right,up");
                sendMessage("keyboard:key,Up,up");
                sendMessage("keyboard:key,Down,up");
            }
            return true;
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (Constants.LEFT_BUTTON.equals(volumeUpKey.getValue())) {
                sendMessage("leftButton:down");
            } else if (Constants.RIGHT_BUTTON.equals(volumeUpKey.getValue())) {
                sendMessage("rightButton:down");
            } else {
                sendMessage("keyboard:key," + volumeUpKey.getValue() + ",down");
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (Constants.LEFT_BUTTON.equals(volumeDownKey.getValue())) {
                sendMessage("leftButton:down");
            } else if (Constants.RIGHT_BUTTON.equals(volumeDownKey.getValue())) {
                sendMessage("rightButton:down");
            } else {
                sendMessage("keyboard:key," + volumeDownKey.getValue() + ",down");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (Constants.LEFT_BUTTON.equals(volumeDownKey.getValue())) {
                sendMessage("leftButton:release");
            } else if (Constants.RIGHT_BUTTON.equals(volumeDownKey.getValue())) {
                sendMessage("rightButton:release");
            } else {
                sendMessage("keyboard:key,"+volumeDownKey.getValue()+",up");
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (Constants.LEFT_BUTTON.equals(volumeUpKey.getValue())) {
                sendMessage("leftButton:release");
            } else if (Constants.RIGHT_BUTTON.equals(volumeUpKey.getValue())) {
                sendMessage("rightButton:release");
            } else {
                sendMessage("keyboard:key,"+volumeUpKey.getValue()+",up");
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.keyboardmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.keyboard_help:
                help();
                break;
            case R.id.keyboard_volume_down:
                volumeSetting(volumeDownKey);
                break;
            case R.id.keyboard_volume_up:
                volumeSetting(volumeUpKey);
                break;
            case R.id.re_back:
                finish();
                break;
            case R.id.exit:
                doExit();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void help(){
        new AlertDialog.Builder(KeyBoardControlActivity.this).setTitle("使用帮助")
                .setMessage("本页面可进行信息的发送 其中DOS发送是在DOS窗口下 信息的发送   \n使用设置 可设置音量键的操作 以方便你的使用和操作")
                .setPositiveButton("确定", (dialog, whichButton) -> {})
                .setNegativeButton("返回", (dialog, which) -> {}).show();
    }

}
