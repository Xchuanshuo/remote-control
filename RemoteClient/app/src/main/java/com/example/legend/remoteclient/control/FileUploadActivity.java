package com.example.legend.remoteclient.control;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.legend.common.AvatarFile;
import com.example.legend.common.Constants;
import com.example.legend.common.packet.FileListRequestPacket;
import com.example.legend.common.packet.FileListResponsePacket;
import com.example.legend.common.packet.FileUploadRequestPacket;
import com.example.legend.remoteclient.R;
import com.example.legend.remoteclient.control.adapter.FileListAdapter;
import com.example.legend.remoteclient.core.FileApi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.legend.common.Constants.FOLDER;

/**
 * @author legend
 */
public class FileUploadActivity extends BaseControlActivity {

    private Button mBackBtn;
    private TextView mPathTv;
    private FileListAdapter mAdapter;
    private List<AvatarFile> avatarFileList = new ArrayList<>();

    @Override
    public int getLayout() {
        return R.layout.activity_file_upload;
    }

    @Override
    protected void initView() {
        this.mAdapter = new FileListAdapter(this, avatarFileList);
        this.mBackBtn = findViewById(R.id.btn_back);
        this.mPathTv = findViewById(R.id.tv_path);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mAdapter.setClickListener(position -> {
            AvatarFile avatarFile = avatarFileList.get(position);
            if (FOLDER.equals(avatarFile.getType())) {
                mPathTv.setText(avatarFile.getPath());
                sendMessage(new FileListRequestPacket(avatarFile.getPath(), Constants.LOCAL));
            } else {
                File file = new File(avatarFile.getPath());
                FileUploadRequestPacket requestPacket =
                        new FileUploadRequestPacket(avatarFile.getPath(), file.length());
                requestPacket.setAttach(file.getName());
                sendMessage(requestPacket);
            }
        });
        mBackBtn.setOnClickListener(v -> {
            String path = mPathTv.getText().toString();
            if (path.replace(FileApi.getExternalStoragePath(),"").length() > 0
                    && path.startsWith(FileApi.getExternalStoragePath())) {
                path = path.substring(0, path.lastIndexOf('/'));
                mPathTv.setText(path);
                sendMessage(new FileListRequestPacket(path, Constants.LOCAL));
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        String path = FileApi.getExternalStoragePath();
        mPathTv.setText(path);
        sendMessage(new FileListRequestPacket(path, Constants.LOCAL));
    }

    @Override
    public void receiveFileList(FileListResponsePacket packet) {
        super.receiveFileList(packet);
        List<AvatarFile> files = packet.data();
        avatarFileList.clear();
        avatarFileList.addAll(files);
        Log.d(FileUploadActivity.class.getName(), avatarFileList.size()+"");
        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
    }
}
