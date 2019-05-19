package com.example.legend.remoteclient.control;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.example.legend.common.Constants;
import com.example.legend.remoteclient.R;

/**
 * @author legend
 */
@SuppressWarnings("AlibabaUndefineMagicConstant")
public class MouseControlActivity extends BaseControlActivity {

    /** 发送的鼠标移动的差值 */
    private static float mx = 0;
    private static float my = 0;
    /** 记录上次鼠标的位置 */
    private static float lx = 0;
    private static float ly = 0;
    /** 手指第一次接触屏幕时的坐标 */
    private static float fx = 0;
    private static float fy = 0;
    /** 鼠标左键移动初始化坐标 */
    private static float lbx = 0;
    private static float lby = 0;

    private FrameLayout mLeftButton;
    private FrameLayout mMiddleButton;
    private FrameLayout mRightButton;
    private FrameLayout mTouch;

    @Override
    public int getLayout() {
        return R.layout.activity_mouse_control;
    }

    @Override
    protected void initView() {
        mTouch = findViewById(R.id.touch);
        mLeftButton = findViewById(R.id.btn_left);
        mMiddleButton = findViewById(R.id.btn_middle);
        mRightButton = findViewById(R.id.btn_right);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        mTouch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                onMouseMove(event);
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onMouseDown(event);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                onMouseUp(event);
            }
            return true;
        });
        mLeftButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onLeftButton("down");
                mLeftButton.setBackgroundResource(R.drawable.zuoc);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                onLeftButton("release");
                lbx = 0;
                lby = 0;
                mLeftButton.setBackgroundResource(R.drawable.zuo);
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                moveMouseWithSecondFinger(event);
            }
            return true;
        });
        mRightButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onRightButton("down");
                mRightButton.setBackgroundResource(R.drawable.youc);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                onRightButton("release");
                mRightButton.setBackgroundResource(R.drawable.you);
            }
            return true;
        });
        mMiddleButton.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                onMiddleButtonDown(event);
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                onMiddleButtonMove(event);
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                break;
            case R.id.keyboard:
                startActivity(new Intent(this, KeyBoardControlActivity.class));
                break;
            case R.id.hand_model:
                break;
            case R.id.volume_down:
                volumeSetting(volumeDownKey);
                break;
            case R.id.volume_up:
                volumeSetting(volumeUpKey);
                break;
            case R.id.exit:
                doExit();
                break;
            default: break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (Constants.LEFT_BUTTON.equals(volumeUpKey.getValue())) {
                sendMessage("leftButton:click");
            } else if (Constants.RIGHT_BUTTON.equals(volumeUpKey.getValue())) {
                sendMessage("rightButton:click");
            } else {
                sendMessage("keyboard:key," + volumeUpKey.getValue() + ",click");
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (Constants.LEFT_BUTTON.equals(volumeDownKey.getValue())) {
                sendMessage("leftButton:click");
            } else if (Constants.RIGHT_BUTTON.equals(volumeDownKey.getValue())) {
                sendMessage("rightButton:click");
            } else {
                sendMessage("keyboard:key," + volumeDownKey.getValue() + ",click");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onMouseMove(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        mx = x - lx;
        my = y - ly;
        lx = x;
        ly = y;
        if (mx != 0 && my != 0) {
            this.sendMouseEvent(Constants.MOUSE, mx, my);
        }
    }

    private void onMouseDown(MotionEvent event) {
        // 手机第一次连接时
        lx = event.getX();
        ly = event.getY();
        fx = event.getX();
        fy = event.getY();
    }

    private void onMouseUp(MotionEvent event) {
        if (fx == event.getX() && fy == event.getY()) {
            sendMessage("leftButton:down");
            sendMessage("leftButton:release");
        }
    }

    private void onLeftButton(String type) {
        String str = "leftButton" + ":" + type;
        sendMessage(str);
    }

    private void onRightButton(String type) {
        String str = "rightButton" + ":" + type;
        sendMessage(str);
    }

    private void onMiddleButtonDown(MotionEvent event) {
        ly = event.getY();
    }

    private void onMiddleButtonMove(MotionEvent event) {
        float y = event.getY();
        my =  y - ly;
        ly = y;
        // 减少发送次数
        if (my > 3 || my < -3) {
            String str = Constants.MOUSE_WHEEL + ":" + my;
            sendMessage(str);
        }
    }

    private void sendMouseEvent(String type, float x, float y) {
        String str = type + ":" + x + "," + y;
        sendMessage(str);
    }

    private void moveMouseWithSecondFinger(MotionEvent event) {
        int count = event.getPointerCount();
        if (count == 2) {
            if (lbx == 0 && lby == 0) {
                lbx = event.getX(1);
                lby = event.getY(1);
                return;
            }
            float x = event.getX(1);
            float y = event.getY(1);
            sendMouseEvent(Constants.MOUSE, x - lbx, y - lby);
            lbx = x;
            lby = y;
        }
        if (count == 1) {
            lbx = 0;
            lby = 0;
        }
    }
}
