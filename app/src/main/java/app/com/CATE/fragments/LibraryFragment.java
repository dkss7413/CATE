package app.com.CATE.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

import app.com.CATE.DetailsActivity;
import app.com.CATE.MainActivity;
import app.com.CATE.TwitchActivity;
import app.com.CATE.adapters.VideoPostAdapter;
import app.com.CATE.interfaces.OnItemClickListener;
import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.models.YoutubeDataModel;
import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LibraryFragment extends Fragment {
    RecyclerView list_horizontal_videoLike;
    VideoPostAdapter adapter = null;
    String userID;
    public static ArrayList<YoutubeDataModel> listData = new ArrayList<>();

    public LibraryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        userID = MainActivity.strName;

        list_horizontal_videoLike = view.findViewById(R.id.list_horizontal_videoLike);
        list_horizontal_videoLike.setAdapter(adapter);

        initList(listData);
        setList();

        view.findViewById(R.id.likeVideoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MainActivity.likeVideoIntent);
            }
        });

        view.findViewById(R.id.commentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(MainActivity.LibraryCommentIntent);
            }
        });

        return view;
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        list_horizontal_videoLike.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false));
        adapter = new VideoPostAdapter(getActivity(), mListData, 1, new OnItemClickListener() {
            @Override
            public void onItemClick(YoutubeDataModel item) {
                final YoutubeDataModel youtubeDataModel = item;
                if (youtubeDataModel.getVideo_kind().equals("YOUTUBE")) { //유튜브 플레이어
                    Retrofit retrofit=new Retrofit.Builder()
                            .baseUrl(RetrofitService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitService retrofitService=retrofit.create(RetrofitService.class);
                    Call<JsonObject> call=retrofitService.MakeLikeTable(MainActivity.strName,youtubeDataModel.getVideo_index());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject jsonObject = response.body();
                            Intent intent = new Intent(getActivity(), DetailsActivity.class);
                            intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                            intent.putExtra("u_v_status", Objects.requireNonNull(jsonObject).get("status").getAsInt());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                        }
                    });
                }
                if (youtubeDataModel.getVideo_kind().equals("TWITCH")) {
                    Retrofit retrofit=new Retrofit.Builder()
                            .baseUrl(RetrofitService.URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitService retrofitService=retrofit.create(RetrofitService.class);
                    Call<JsonObject> call=retrofitService.MakeLikeTable(MainActivity.strName,youtubeDataModel.getVideo_index());
                    call.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                            JsonObject jsonObject=response.body();
                            Intent intent = new Intent(getActivity(), TwitchActivity.class);
                            intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                            intent.putExtra("u_v_status", Objects.requireNonNull(jsonObject).get("status").getAsInt());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                        }
                    });
                }
            }
        });
        list_horizontal_videoLike.setAdapter(adapter);
    }

    private void setList(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getLikeVideo(userID).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                listData = new ArrayList<>();

                for(int i = 0; i < Objects.requireNonNull(response.body()).size(); i++) {
                    JsonObject object = response.body().get(i).getAsJsonObject();

                    YoutubeDataModel youtubeObject = new YoutubeDataModel();
                    String thumbnail;
                    String video_id = "";
                    String cateName, video_kind, cateDetail;
                    int video_index, likes, dislikes;

                    cateName = object.get("title").getAsString();
                    video_kind = object.get("kind").getAsString();
                    cateDetail = object.get("url").getAsString();
                    thumbnail = object.get("thumbnail").getAsString().replace("\\", "");
                    video_index = Integer.parseInt(object.get("id").getAsString());
                    likes = Integer.parseInt(object.get("likes").getAsString());
                    dislikes = Integer.parseInt(object.get("dislikes").getAsString());

                    if (video_kind.equals("YOUTUBE")) {
                        video_id = cateDetail.substring(cateDetail.indexOf("=") + 1);
                    }
                    if (video_kind.equals("TWITCH")) {
                        String[] split = cateDetail.split("/");
                        video_id = split[split.length - 1];
                    }

                    youtubeObject.setVideo_index(video_index);
                    youtubeObject.setTitle(cateName);
                    youtubeObject.setThumbnail(thumbnail);
                    youtubeObject.setVideo_id(video_id);
                    youtubeObject.setVideo_kind(video_kind);
                    youtubeObject.setLikes(likes);
                    youtubeObject.setDislikes(dislikes);

                    listData.add(youtubeObject);
                    initList(listData);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {

            }
        });
    }
}
