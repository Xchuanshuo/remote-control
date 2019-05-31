package com.example.legend.common.task;

import com.example.legend.common.AvatarFile;
import com.example.legend.common.Constants;
import com.example.legend.common.ReceiveCallback;
import com.example.legend.common.Utils;
import com.example.legend.common.packet.FileListRequestPacket;
import com.example.legend.common.packet.FileListResponsePacket;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.legend.common.Constants.FOLDER;

/**
 * @author Legend
 * @data by on 19-5-14.
 * @description
 */
public class GetLocalFilesTask implements Runnable {

    private String path;
    private ReceiveCallback<FileListResponsePacket> callback;
    private List<AvatarFile> avatarFiles = new ArrayList<>();

    public GetLocalFilesTask(FileListRequestPacket packet, ReceiveCallback<FileListResponsePacket> callback) {
        this.path = packet.data();
        this.callback = callback;
    }

    @Override
    public void run() {
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) return;
        if (files.length == 0) {
            callback.receiveMessage(new FileListResponsePacket(avatarFiles));
            return;
        }
        for (File f : files) {
            String heading = f.getName();
            long lastModifyMills = f.lastModified();
            String lastModifiedDate = Utils.getDate(lastModifyMills, "dd MMM yyyy hh:mm a");
            String filePath = f.getAbsolutePath();
            int icon = 0;
            String itemOrSize, type = "file";
            if (f.isDirectory()) {
                type = "folder";
                File[] temp = f.listFiles();
                if (temp == null) {
                    itemOrSize = "0 item";
                } else {
                    itemOrSize = temp.length + " items";
                }
            } else {
                itemOrSize = Utils.getSize(f.length());
                if (heading.endsWith("jpg") || heading.endsWith("jpeg")
                        || heading.endsWith("png") || heading.endsWith("webp")) {
                    type = Constants.IMAGE;
                } else if (heading.endsWith(Constants.MP3)){
                    type = Constants.MP3;
                } else if (heading.endsWith(Constants.PDF)) {
                    type = Constants.PDF;
                }
            }
            String subHeading = itemOrSize + " " + lastModifiedDate;
            avatarFiles.add(new AvatarFile(icon, heading, subHeading, filePath, type));
        }
        Collections.sort(avatarFiles, (o1, o2) -> {
            if (FOLDER.equals(o1.getType()) && FOLDER.equals(o2.getType())) {
                return o1.getHeading().compareToIgnoreCase(o2.getHeading());
            }
            if (FOLDER.equals(o1.getType())) return -1;
            if (FOLDER.equals(o2.getType())) return 1;
            return o2.getType().compareToIgnoreCase(o1.getType());
        });
        if (callback != null) {
            callback.receiveMessage(new FileListResponsePacket(avatarFiles));
        }
    }
}
