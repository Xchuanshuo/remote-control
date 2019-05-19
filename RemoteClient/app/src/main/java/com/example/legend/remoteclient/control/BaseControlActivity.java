package com.example.legend.remoteclient.control;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.legend.common.Constants;
import com.example.legend.common.ReceiveCallback;
import com.example.legend.common.core.TaskHandleCenter;
import com.example.legend.common.packet.FileDownloadRequestPacket;
import com.example.legend.common.packet.FileDownloadResponsePacket;
import com.example.legend.common.packet.FileListRequestPacket;
import com.example.legend.common.packet.FileListResponsePacket;
import com.example.legend.common.packet.FileUploadResponsePacket;
import com.example.legend.common.packet.Packet;
import com.example.legend.common.packet.StringRequestPacket;
import com.example.legend.common.task.GetLocalFiles;
import com.example.legend.common.task.GetLocalFilesTask;
import com.example.legend.common.task.MakeConnection;
import com.example.legend.remoteclient.R;
import com.example.legend.remoteclient.core.FileSendTask;

import java.io.File;
import java.io.IOException;

import static com.example.legend.common.Constants.FILE_DOWNLOAD_REQUEST;
import static com.example.legend.common.Constants.FILE_DOWNLOAD_RESPONSE;
import static com.example.legend.common.Constants.FILE_LIST_REQUEST;
import static com.example.legend.common.Constants.FILE_LIST_RESPONSE;
import static com.example.legend.common.Constants.FILE_UPLOAD_RESPONSE;

/**
 * @author Legend
 * @data by on 19-4-13.
 * @description
 */
public abstract class BaseControlActivity extends AppCompatActivity implements ReceiveCallback<Packet> {

    Key volumeUpKey = new VolumeUpKey();
    Key volumeDownKey = new VolumeDownKey();
    protected TaskHandleCenter handleCenter = TaskHandleCenter.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        handleCenter.setReceiveCallback(this);
        initView();
        initData();
        initListener();
    }

    protected void initData() {}

    public abstract int getLayout();

    protected void initView() {}

    protected void initListener() {}

    public void commit(Runnable runnable) {
        handleCenter.commit(runnable);
    }

    public void sendMessage(String str) {
        this.sendMessage(new StringRequestPacket(str));
    }

    public void sendMessage(Packet packet) {
        if (packet.identifier == Constants.REMOTE) {
            handleCenter.sendPacket(packet);
        } else {
            new GetLocalFiles((FileListRequestPacket) packet, this::receiveFileList);
        }
    }

    @Override
    public void receiveMessage(Packet packet) {
        runOnUiThread(() -> {
            String type = packet.type();
            switch (type) {
                case FILE_LIST_REQUEST:
                    dealFileListRequest((FileListRequestPacket) packet);
                    break;
                case FILE_LIST_RESPONSE:
                    receiveFileList((FileListResponsePacket) packet);
                    break;
                case FILE_DOWNLOAD_REQUEST:
                    dealFileDownloadRequest((FileDownloadRequestPacket) packet);
                    break;
                case FILE_UPLOAD_RESPONSE:
                    dealFileUploadResponse((FileUploadResponsePacket) packet);
                    break;
                case FILE_DOWNLOAD_RESPONSE:
                    receiveFile((FileDownloadResponsePacket) packet);
                    break;
                default: break;
            }
        });
    }

    public void dealFileListRequest(FileListRequestPacket packet) {
        new GetLocalFilesTask(packet, this::sendMessage);
    }

    public void dealFileDownloadRequest(FileDownloadRequestPacket packet) {
        File file = new File(packet.data());
        if (file.length() == 0) {
            return;
        }
        sendMessage(new FileUploadResponsePacket(file.getName(), file.length()));
        try {
            String[] params = new String[]{packet.data(), packet.length+""};
            new FileSendTask(this, false).execute(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dealFileUploadResponse(FileUploadResponsePacket packet) {
        new MakeConnection(socket -> {
            handleCenter.setSocket(socket);
            runOnUiThread(() -> {
                try {
                    String[] params = new String[]{packet.data(), packet.length+""};
                    new FileSendTask(this).execute(params);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void receiveFileList(FileListResponsePacket packet) {}

    public void receiveFile(FileDownloadResponsePacket packet) {}

    protected void volumeSetting(Key key) {
        final String[] items = {"鼠标左键","鼠标右键", "Ctrl键", "Z键","空格键","Up键"};
        new AlertDialog.Builder(this)
                .setTitle("选择按键")
                .setItems(items, (dialog, item) -> {
                    Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                    switch (item) {
                        case 0:
                            key.setValue("leftButton");
                            break;
                        case 1:
                            key.setValue("rightButton");
                            break;
                        case 2:
                            key.setValue("Ctrl");
                            break;
                        case 3:
                            key.setValue("Z");
                            break;
                        case 4:
                            key.setValue("Space");
                            break;
                        case 5:
                            key.setValue("Up");
                            break;
                        default: break;
                    }
                }).show();
    }

    class Key {
        private String value;

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    class VolumeUpKey extends Key {
        VolumeUpKey() {
            setValue("leftButton");
        }
    }

    class VolumeDownKey extends Key {
        VolumeDownKey() {
            setValue( "rightButton");
        }
    }

    protected void doExit() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.exit_message))
                .setPositiveButton(getString(R.string.confirm), (dialog, which) -> finish())
                .setNeutralButton(getString(R.string.cancel), (dialog, which) -> {})
                .show();
    }

}
