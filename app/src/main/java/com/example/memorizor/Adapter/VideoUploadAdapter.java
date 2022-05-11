package com.example.memorizor.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.R;

import java.util.List;

public class VideoUploadAdapter extends RecyclerView.Adapter<VideoUploadAdapter.ViewHolder> {

    private Context mContext;
    private List<Uri> mVideoUploadUris;

    public VideoUploadAdapter(Context mContext, List<Uri> mVideoUploadUris) {
        this.mContext = mContext;
        this.mVideoUploadUris = mVideoUploadUris;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_upload_item, parent, false);
        return new VideoUploadAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri videoUri = mVideoUploadUris.get(position);

        MediaController mediaController = new MediaController(mContext);
        mediaController.setAnchorView(holder.videoView);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(videoUri);
        holder.videoView.requestFocus();
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                holder.videoView.pause();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoUploadUris.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public VideoView videoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.video_view);
        }
    }

}

