package app.com.CATE;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import app.com.CATE.adapters.VideoPostAdapter;
import app.com.CATE.fragments.LibraryFragment;
import app.com.CATE.interfaces.OnItemClickListener;
import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.models.YoutubeDataModel;
import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LibraryLikeVideoActivity extends AppCompatActivity {
    RecyclerView list_videoLike;
    VideoPostAdapter adapter = null;
    String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_video_like);

        userID = MainActivity.strName;

        list_videoLike = findViewById(R.id.list_videoLike);
        list_videoLike.setAdapter(adapter);

        initList(LibraryFragment.listData);
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        list_videoLike.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new VideoPostAdapter(getApplicationContext(), mListData, new OnItemClickListener() {
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
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject jsonObject = response.body();
                            Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                            intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                            intent.putExtra("u_v_status", jsonObject.get("status").getAsInt());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

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
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            JsonObject jsonObject=response.body();
                            Intent intent = new Intent(getApplicationContext(), TwitchActivity.class);
                            intent.putExtra(YoutubeDataModel.class.toString(), youtubeDataModel);
                            intent.putExtra("u_v_status", jsonObject.get("status").getAsInt());
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
                }
            }
        });
        list_videoLike.setAdapter(adapter);
    }
}
