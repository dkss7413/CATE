package app.com.CATE.fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.TwitchActivity;
import app.com.CATE.adapters.HorizontalCategoryAdapter;
import app.com.CATE.adapters.VideoPostAdapter;
import app.com.CATE.DetailsActivity;
import app.com.CATE.MainActivity;
import app.com.CATE.interfaces.OnArrayClickListener;
import app.com.youtubeapiv3.R;
import app.com.CATE.interfaces.OnItemClickListener;
import app.com.CATE.models.YoutubeDataModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class HomeFragment extends Fragment {

    private RecyclerView mList_videos = null;
    private VideoPostAdapter adapter = null;
    private ArrayList<YoutubeDataModel> mListData = new ArrayList<>();
    private ArrayList<YoutubeDataModel> nListData = new ArrayList<>();

    public static int A;
    private boolean mLock;
    String userID;
    String category_selected;
    boolean collision=true;

    //가로 카테고리
    private RecyclerView horizontalListView;
    private View preView;

    //정렬 기준
    String sortBy;

    public MainActivity mainActivity;
    private ProgressBar progressBar, progressBarStart;
    ArrayList<String> arrayList = new ArrayList<>();

    PullRefreshLayout loading;

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        horizontalListView.setLayoutManager(layoutManager);
    }

    public HomeFragment() {
// Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();
        mList_videos = view.findViewById(R.id.mList_videos);
        mListData = mainActivity.listData;
        horizontalListView = view.findViewById(R.id.mList_horizontal_category);
        progressBar = view.findViewById(R.id.progressBarHome);
        progressBarStart = view.findViewById(R.id.progressBarFirst);
        progressBar.setVisibility(View.GONE);
        progressBarStart.setVisibility(View.GONE);

        initList(nListData);

        mLock=true;

        userID = MainActivity.strName;

        //스피너 생성 및 클릭시 동작 지정
        Spinner spinner = view.findViewById(R.id.spinnerSort);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBy = ""+parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortBy = "인기순";
            }

        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getCategory(MainActivity.strName).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call,@NonNull Response<JsonArray> response) {

                try {


                    arrayList.add("전체");
                    for(int i = 0; i < Objects.requireNonNull(response.body()).size(); i++) {
                        arrayList.add(response.body().get(i).getAsJsonObject().get("category_name").getAsString());
                    }

                    init(arrayList);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "정렬 기준 초기화 실패", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {

            }
        });

        loading= (PullRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);

        loading.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);

        loading.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
        //Thread - 1초 후 로딩 종료
                init(arrayList);
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.setRefreshing(false);
                    }
                },1000);
            }
        });

        mList_videos.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, final int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount();

                if (lastVisibleItemPosition == itemTotalCount-1 && mLock==true) {

                        category_scroll_plus(lastVisibleItemPosition + 1);

                    //리스트 마지막(바닥) 도착!!!!! 다음 페이지 데이터 로드!!
                }

            }
        });

        return view;
    }

    private void category_scroll_plus(final int start){
        mLock=false;
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        retrofitService.getCategoryVideo(category_selected, sortBy).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                try {
                    for(int i=start; i < start+15; i++) {
                        A=i;
                        JsonObject object = Objects.requireNonNull(response.body()).get(i).getAsJsonObject();

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

                        mListData.add(youtubeObject);
//                        initList(mListData);
                    } new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            mLock=true;
                            progressBar.setVisibility(View.GONE);
                        }
                    },1000);
                } catch(IndexOutOfBoundsException ea){

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(A==start){
                                Toast.makeText(getContext(), "더이상 동영상이 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                            adapter.notifyDataSetChanged();
                            mLock=true;
                            progressBar.setVisibility(View.GONE);
                        }
                    },1000);
                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                mLock=true;
            }
        });
    }

    private void init(ArrayList<String> arrayList) {

        HorizontalCategoryAdapter adapter2 = new HorizontalCategoryAdapter(getContext(), arrayList, new OnArrayClickListener(){
            @Override
            public void onArrayClick(String category, View view) {
                if(preView != null) preView.setBackground(ContextCompat.getDrawable(Objects.requireNonNull(getContext()), R.drawable.border));
                if(view != null) {
                    preView = view;
                    view.setBackgroundColor(Color.LTGRAY);
                    progressBarStart.setVisibility(View.VISIBLE);
                }

                category_selected=category;
                mListData = new ArrayList<>();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(RetrofitService.URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                retrofitService.getCategoryVideo(category, sortBy).enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                        mListData = new ArrayList<>();
                        try {
                            for (int i = 0; i < 15; i++) {
                                JsonObject object = Objects.requireNonNull(response.body()).get(i).getAsJsonObject();

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

                                mListData.add(youtubeObject);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initList(mListData);
                                    progressBar.setVisibility(View.GONE);
                                    progressBarStart.setVisibility(View.GONE);
                                }
                            }, 1000);
                        } catch (IndexOutOfBoundsException ea) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initList(mListData);
                                    progressBar.setVisibility(View.GONE);
                                    progressBarStart.setVisibility(View.GONE);
                                }
                            }, 1000);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {

                    }
                });
            }
        });
        horizontalListView.setAdapter(adapter2);
    }

    private void initList(ArrayList<YoutubeDataModel> mListData) {
        progressBar.setVisibility(View.GONE);

        adapter = new VideoPostAdapter(getActivity(), mListData, new OnItemClickListener() {
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
        mList_videos.setAdapter(adapter);
        mList_videos.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}