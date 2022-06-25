package com.example.memorizor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memorizor.Model.User;
import com.example.memorizor.ModeratorUserDetailsActivity;
import com.example.memorizor.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SimpleUserAdapter extends RecyclerView.Adapter<SimpleUserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;

    public SimpleUserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public SimpleUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.simple_user_item, parent, false);
        return new SimpleUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleUserAdapter.ViewHolder holder, int position) {
        int poz = position;
        holder.tv_fullname_user.setText(mUsers.get(poz).getName());
        holder.tv_email_user.setText(mUsers.get(poz).getEmail());

        holder.cardView_user.setTranslationY(-800);
        holder.cardView_user.setAlpha(0);
        holder.cardView_user.animate().translationY(0).alpha(1).setDuration(300).setStartDelay(position*100).start();

        if(mUsers.get(position).getProfileImageUrl().equals("")){

        } else {
            Picasso.get().load(mUsers.get(position).getProfileImageUrl()).placeholder(R.drawable.backgroudn).into(holder.image_profile_user);
        }

        holder.cardView_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ModeratorUserDetailsActivity.class);
                intent.putExtra("userId", mUsers.get(poz).getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_fullname_user;
        public TextView tv_email_user;
        public CircleImageView image_profile_user;
        public CardView cardView_user;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_fullname_user = itemView.findViewById(R.id.tv_fullname_user);
            tv_email_user = itemView.findViewById(R.id.tv_email_user);
            image_profile_user = itemView.findViewById(R.id.image_profile_user);
            cardView_user = itemView.findViewById(R.id.cardView_user);
        }
    }

}
