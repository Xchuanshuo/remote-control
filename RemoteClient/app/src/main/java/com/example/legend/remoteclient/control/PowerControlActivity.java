package com.example.legend.remoteclient.control;

import android.widget.Button;

import com.example.legend.common.Constants;
import com.example.legend.remoteclient.R;

/**
 * @author legend
 */
public class PowerControlActivity extends BaseControlActivity {

    private Button mRestartBtn;
    private Button mHibernateBtn;
    private Button mShutDownBtn;

    @Override
    public int getLayout() {
        return R.layout.activity_power_control;
    }

    @Override
    protected void initView() {
        mRestartBtn = findViewById(R.id.btn_restart);
        mHibernateBtn = findViewById(R.id.btn_hibernate);
        mShutDownBtn = findViewById(R.id.btn_shutdown);
    }

    @Override
    protected void initListener() {
        mRestartBtn.setOnClickListener(v -> sendMessage(Constants.POWER+":"+ Constants.POWER_RESTART));
        mHibernateBtn.setOnClickListener(v -> sendMessage(Constants.POWER+":" + Constants.POWER_HIBERNATE));
        mShutDownBtn.setOnClickListener(v -> sendMessage(Constants.POWER+":" + Constants.POWER_TURN_OFF));
    }
}
