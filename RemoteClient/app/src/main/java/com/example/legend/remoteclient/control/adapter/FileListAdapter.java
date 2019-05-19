package com.example.legend.remoteclient.control.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.legend.common.AvatarFile;
import com.example.legend.remoteclient.R;

import java.util.List;

import static com.example.legend.common.Constants.FILE;
import static com.example.legend.common.Constants.FOLDER;
import static com.example.legend.common.Constants.IMAGE;
import static com.example.legend.common.Constants.MP3;
import static com.example.legend.common.Constants.PDF;

/**
 * @author Legend
 * @data by on 19-5-13.
 * @description
 */
public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.Holder> {

    private Context mContext;
    private List<AvatarFile> avatarFiles;
    private ClickListener clickListener;

    public FileListAdapter(Context context, List<AvatarFile> avatarFiles) {
        this.mContext = context;
        this.avatarFiles = avatarFiles;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_file_list, viewGroup, false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        AvatarFile avatarFile = avatarFiles.get(i);
        String type = avatarFile.getType();
        if (IMAGE.equals(type)) {
            holder.mIconImg.setImageResource(R.mipmap.image);
        } else if (MP3.equals(type)) {
            holder.mIconImg.setImageResource(R.mipmap.music_png);
        } else if (PDF.equals(type)) {
            holder.mIconImg.setImageResource(R.mipmap.pdf);
        } else if (FILE.equals(type)) {
            holder.mIconImg.setImageResource(R.mipmap.file);
        } else if (FOLDER.equals(type)) {
            holder.mIconImg.setImageResource(R.mipmap.folder);
        }
        holder.mItem.setOnClickListener(v -> {
            Toast.makeText(mContext, avatarFiles.get(i).getHeading()+"", Toast.LENGTH_SHORT).show();
            if (clickListener != null) {
                if (i != -1) {
                    clickListener.onItemClick(i);
                }
            }
        });
        holder.mHeadingTv.setText(avatarFile.getHeading());
        holder.mSubHeadingTv.setText(avatarFile.getSubheading());
    }

    @Override
    public int getItemCount() {
        return avatarFiles.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        LinearLayout mItem;
        ImageView mIconImg;
        TextView mHeadingTv, mSubHeadingTv;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mItem = itemView.findViewById(R.id.linear_layout_item);
            mIconImg = itemView.findViewById(R.id.img_icon);
            mHeadingTv = itemView.findViewById(R.id.tv_heading);
            mSubHeadingTv = itemView.findViewById(R.id.tv_subheading);
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position);
    }
}
