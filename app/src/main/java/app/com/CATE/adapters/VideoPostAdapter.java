package app.com.CATE.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.youtubeapiv3.R;
import app.com.CATE.interfaces.OnItemClickListener;
import app.com.CATE.models.YoutubeDataModel;


public class VideoPostAdapter extends RecyclerView.Adapter<VideoPostAdapter.YoutubePostHolder> {

    private ArrayList<YoutubeDataModel> dataSet;
    private Context mContext;
    private final OnItemClickListener listener;
    private int mode;

    public VideoPostAdapter(Context mContext, ArrayList<YoutubeDataModel> dataSet, OnItemClickListener listener) {
        this.dataSet = dataSet;
        this.mContext = mContext;
        this.listener = listener;
    }

    public VideoPostAdapter(Context mContext, ArrayList<YoutubeDataModel> dataSet, int mode, OnItemClickListener listener) {
        this.dataSet = dataSet;
        this.mContext = mContext;
        this.listener = listener;
        this.mode = mode;
    }

    @Override
    public YoutubePostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_post_layout,parent,false);
        return new YoutubePostHolder(view);
    }

    @Override
    public void onBindViewHolder(YoutubePostHolder holder, int position) {

        //set the views here
        TextView textViewTitle = holder.textViewTitle;
        TextView videoLikes = holder.videoLikes;
        TextView videoDislikes = holder.videoDislikes;
        ImageView ImageThumb = holder.ImageThumb;
        ImageView imageView2 = holder.imageView2;
        ImageView imageView3 = holder.imageView3;

        YoutubeDataModel object = dataSet.get(position);

        textViewTitle.setText(object.getTitle());
        videoLikes.setText(String.valueOf(object.getLikes()));
        videoDislikes.setText(String.valueOf(object.getDislikes()));
        holder.bind(dataSet.get(position), listener);

        Picasso.with(mContext).load(object.getThumbnail()).into(ImageThumb);

        if(mode == 1){
            LinearLayout youtube_post_layout = holder.youtube_post_layout;
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) youtube_post_layout.getLayoutParams();
            lp.height = 550;
            lp.width = 750;
            youtube_post_layout.setLayoutParams(lp);

            textViewTitle.setTextSize(20);
            videoLikes.setVisibility(View.GONE);
            videoDislikes.setVisibility(View.GONE);
            imageView2.setVisibility(View.GONE);
            imageView3.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

     static class YoutubePostHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView videoLikes, videoDislikes;
        TextView textViewDate;
        ImageView ImageThumb;
        LinearLayout youtube_post_layout;
        ImageView imageView2;
        ImageView imageView3;

         YoutubePostHolder(View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.videoLikes = itemView.findViewById(R.id.textVideoLikes);
            this.videoDislikes = itemView.findViewById(R.id.textVideoDislikes);
            this.textViewDate = itemView.findViewById(R.id.textViewDate);
            this.ImageThumb = itemView.findViewById(R.id.ImageThumb);
            this.youtube_post_layout = itemView.findViewById(R.id.youtube_post_layout);
            this.imageView2 = itemView.findViewById(R.id.imageView2);
            this.imageView3 = itemView.findViewById(R.id.imageView3);
        }

         void bind(final YoutubeDataModel item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
