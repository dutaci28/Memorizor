package com.example.memorizor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.CourseActivity;
import com.example.memorizor.Model.Video;
import com.example.memorizor.R;
import com.example.memorizor.VideoActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder>{
    private Context mContext;
    private List<Video> mVideos;

    public VideoAdapter(Context mContext, List<Video> mVideos) {
        this.mContext = mContext;
        this.mVideos = mVideos;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.video_item, parent, false);
        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int poz = position;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mVideos.get(poz).getVideoUrl(), new HashMap<String, String>());
        Bitmap thumbnailImage = retriever.getFrameAtTime(50000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        holder.imageView.setImageBitmap(thumbnailImage);

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(mContext, VideoActivity.class);
                intent.putExtra("videoId", mVideos.get(poz).getVideoId());
                mContext.startActivity(intent);
                return false;
            }
        });
//
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, VideoActivity.class);
//                intent.putExtra("videoId", mVideos.get(poz).getVideoId());
//                mContext.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
