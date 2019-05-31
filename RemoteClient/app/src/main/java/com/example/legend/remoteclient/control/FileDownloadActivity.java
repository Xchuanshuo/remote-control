package com.example.legend.remoteclient.control;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.legend.common.AvatarFile;
import com.example.legend.common.packet.FileListRequestPacket;
import com.example.legend.common.packet.FileListResponsePacket;
import com.example.legend.common.packet.FileRequestPacket;
import com.example.legend.common.packet.FileResponsePacket;
import com.example.legend.remoteclient.R;
import com.example.legend.remoteclient.control.adapter.FileListAdapter;
import com.example.legend.remoteclient.core.FileApi;
import com.example.legend.remoteclient.core.MultiFileDownloadTask;

import java.util.ArrayList;
import java.util.List;

import static com.example.legend.common.Constants.FOLDER;


/**
 * @author legend
 */
public class FileDownloadActivity extends BaseControlActivity {

    private Button mBackBtn;
    private TextView mPathTv;
    private RecyclerView mRecyclerView;
    private FileListAdapter mAdapter;
    private List<AvatarFile> avatarFileList = new ArrayList<>();

    @Override
    public int getLayout() {
        return R.layout.activity_file_download;
    }

    @Override
    protected void initView() {
        this.mAdapter = new FileListAdapter(this, avatarFileList);
        this.mBackBtn = findViewById(R.id.btn_back);
        this.mPathTv = findViewById(R.id.tv_path);
        this.mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initListener() {
        mAdapter.setClickListener(position -> {
            AvatarFile avatarFile = avatarFileList.get(position);
            if (FOLDER.equals(avatarFile.getType())) {
                mPathTv.setText(avatarFile.getPath());
                sendMessage(new FileListRequestPacket(avatarFile.getPath()));
            } else {
                sendMessage(new FileRequestPacket(avatarFile.getPath()));
            }
        });
        mBackBtn.setOnClickListener(v -> {
            String path = mPathTv.getText().toString();
            if (path.replace(FileApi.homeDirectoryPath, "").length() > 0
                    && path.startsWith(FileApi.homeDirectoryPath)) {
                Log.d(FileDownloadActivity.class.getName(), FileApi.homeDirectoryPath);
                path = path.substring(0, path.lastIndexOf('/'));
                mPathTv.setText(path);
                sendMessage(new FileListRequestPacket(path));
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        mPathTv.setText("/");
        sendMessage(new FileListRequestPacket("/"));
    }

    @Override
    public void receiveFileList(FileListResponsePacket packet) {
        super.receiveFileList(packet);
        if (!TextUtils.isEmpty(packet.getAttach())) {
            FileApi.homeDirectoryPath = packet.getAttach();
            mPathTv.setText(FileApi.homeDirectoryPath);
        }
        List<AvatarFile> files = packet.data();
        avatarFileList.clear();
        avatarFileList.addAll(files);
        runOnUiThread(() -> mAdapter.notifyDataSetChanged());
    }

    @Override
    public void receiveFileResponse(FileResponsePacket packet) {
        super.receiveFileResponse(packet);
        new MultiFileDownloadTask(FileApi.getExternalStoragePath() +"/RemoteControl/" ,
                packet, this).start();
    }
}
