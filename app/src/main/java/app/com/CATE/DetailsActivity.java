package app.com.CATE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import app.com.CATE.adapters.CommentAdapter;
import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.models.CommentModel;
import app.com.CATE.models.YoutubeCommentModel;
import app.com.CATE.models.YoutubeDataModel;
import app.com.CATE.requests.BestCommentRequest;
import app.com.CATE.requests.CommentInsertRequest;
import app.com.CATE.requests.CommentRequest;
import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static String GOOGLE_YOUTUBE_API = "AIzaSyBH8szUCt1ctKQabVeQuvWgowaKxHVjn8E";
    private YoutubeDataModel youtubeDataModel = null;
    TextView textViewName,countLike,countDisLike;
    ImageView imageButtonLike,imageButtonDisLike,declaration_posting;

    public static final String VIDEO_ID = "c2UNv38V6y4";
    private YouTubePlayerView mYoutubePlayerView = null;
    private YouTubePlayer mYoutubePlayer = null;
    private ArrayList<YoutubeCommentModel> mListData = new ArrayList<>();
    private RecyclerView mList_videos = null;
    ListView listview;
    public static int video_index;
    int  u_v_status,likes,dislikes;
    public static String userName = "";

    final ArrayList<CommentModel> cListData = new ArrayList<>();
    final ArrayList<CommentModel> cListData2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        userName = MainActivity.strName;

        Intent intent = getIntent();
        youtubeDataModel = intent.getParcelableExtra(YoutubeDataModel.class.toString());
        video_index = youtubeDataModel.getVideo_index();
        likes = youtubeDataModel.getLikes();
        dislikes = youtubeDataModel.getDislikes();
        u_v_status = intent.getIntExtra("u_v_status",0);

        mYoutubePlayerView = findViewById(R.id.youtube_player);
        mYoutubePlayerView.initialize(GOOGLE_YOUTUBE_API, this);

        textViewName = findViewById(R.id.textViewName);

        TextView ss=findViewById(R.id.textViewDate);
        SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd HH:mm");
        Date time = new Date();

        String time1 = format1.format(time);
        ss.setText(time1);
        textViewName.setText(youtubeDataModel.getTitle());

        mList_videos = findViewById(R.id.mList_videos);
        listview = findViewById(R.id.commentList);

        imageButtonLike=findViewById(R.id.imageButtonLike);
        imageButtonDisLike=findViewById(R.id.imageButtonDisLike);
        declaration_posting=findViewById(R.id.declaration_posting);


        //신고하기 기능
        declaration_posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(DetailsActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                builder.setTitle("신고하기");
                builder.setMessage("신고 사유를 적어주세요.");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog pd=ProgressDialog.show(DetailsActivity.this,""," 신고 내용을 보내는 중입니다.");
                                Thread t=new Thread(){
                                    @Override
                                    public void run() {
                                        try {
                                            GMailSender gMailSender = new GMailSender("ghkdua059@gmail.com", "6013861z!");
                                            gMailSender.sendMail("게시글 번호 : "+video_index+" 를(을) 신고합니다.",
                                                    "신고 이유 : "+edittext.getText().toString(), "ghkdua1829@naver.com");
                                            Looper.prepare();
                                            Toast.makeText(DetailsActivity.this, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        } catch (SendFailedException e) {
                                            Toast.makeText(DetailsActivity.this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                        } catch (MessagingException e) {
                                            Toast.makeText(DetailsActivity.this, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Looper.loop();
                                    }
                                };
                                t.start();
                            }
                        });
                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });

        //좋아요 싫어요 상태
        if(u_v_status==1){
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);
            imageButtonLike.setTag(R.drawable.ic_thumb_up_selected_24px);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
        }else if(u_v_status==2){
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
            imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
        }else{
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
            imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
        }

        countLike=findViewById(R.id.countLike);
        countDisLike=findViewById(R.id.countDisLike);

        countLike.setText(String.valueOf(likes));
        countDisLike.setText(String.valueOf(dislikes));
        imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_selected_24px)) {     //좋아요 취소
                    update_likes(5);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) - 1));
                }
                else if(imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_24px) &&imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_selected_24px)){
                    update_likes(3);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString())+1));
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())-1));
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_selected_24px);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
                }
                else{
                    update_likes(1);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);      //좋아요 누르기
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_selected_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString())+1));
                }
            }
        });
        imageButtonDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_selected_24px) ){    //싫어요 취소
                    update_likes(6);
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())-1));
                }
                else if(imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_24px) &&imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_selected_24px)){
                    update_likes(4);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) - 1));
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())+1));
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
                }
                else{             //i가 1일때 싫어요 클릭이 안된 상태
                    update_likes(2);
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);      //싫어요 누르기
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString())+1));
                }
            }
        });


        if (!checkPermissionForReadExtertalStorage()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        final EditText descText = findViewById(R.id.descText);
        Button insertButton = findViewById(R.id.insertButton);


        final Response.Listener<String> responseListenerBest = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    final Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                if (response.startsWith("ï»¿")) {
                                    response = response.substring(3);
                                }

                                JSONArray jsonArray = new JSONArray(response);
                                for(int i=0; i<jsonArray.length(); i++) {
                                    JSONObject commentObject = jsonArray.getJSONObject(i);
                                    String video_id = commentObject.getString("video_id");
                                    String author = commentObject.getString("author");
                                    String _index = commentObject.getString("_index");
                                    String desc = commentObject.getString("desc");
                                    String writetime = commentObject.getString("writetime");
                                    String commentLike = commentObject.getString("commentLike");
                                    String commentDisLike = commentObject.getString("commentDisLike");
                                    String status = commentObject.getString("status");

                                    CommentModel commentModel = new CommentModel(video_id,author,_index, desc,writetime,commentLike,commentDisLike,status,0);
                                    cListData.add(commentModel);
                                }

                                CommentAdapter adapter = new CommentAdapter(cListData,DetailsActivity.this);
                                listview.setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    final CommentRequest commentRequest = new CommentRequest(video_index, userName,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);
                    queue.add(commentRequest);

                    if (response.startsWith("ï»¿")) {
                        response = response.substring(3);
                    }

                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0; i<jsonArray.length(); i++) {
                        JSONObject commentObject = jsonArray.getJSONObject(i);
                        String video_id = commentObject.getString("video_id");
                        String author = commentObject.getString("author");
                        String _index = commentObject.getString("_index");
                        String desc = commentObject.getString("desc");
                        String writetime = commentObject.getString("writetime");
                        String commentLike = commentObject.getString("commentLike");
                        String commentDisLike = commentObject.getString("commentDisLike");
                        String status = commentObject.getString("status");

                        CommentModel commentModel2 = new CommentModel(video_id,author,_index, desc,writetime,commentLike,commentDisLike,status,1);
                        cListData.add(commentModel2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        final BestCommentRequest BestCommentRequest = new BestCommentRequest(video_index, userName,responseListenerBest);
        RequestQueue queue2 = Volley.newRequestQueue(DetailsActivity.this);
        queue2.add(BestCommentRequest);





        insertButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final String desc = descText.getText().toString();

                final Response.Listener<String> responseListenerBest3 = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            final Response.Listener<String> responseListener1 = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        if (response.startsWith("ï»¿")) {
                                            response = response.substring(3);
                                        }
                                        JSONArray jsonArray = new JSONArray(response);
                                        for(int i=0; i<jsonArray.length(); i++) {
                                            JSONObject commentObject = jsonArray.getJSONObject(i);
                                            String video_id = commentObject.getString("video_id");
                                            String author = commentObject.getString("author");
                                            String _index = commentObject.getString("_index");
                                            String desc = commentObject.getString("desc");
                                            String writetime = commentObject.getString("writetime");
                                            String commentLike = commentObject.getString("commentLike");
                                            String commentDisLike = commentObject.getString("commentDisLike");
                                            String status = commentObject.getString("status");

                                            CommentModel commentModel = new CommentModel(video_id,author,_index, desc,writetime,commentLike,commentDisLike,status,0);
                                            cListData2.add(commentModel);
                                        }

                                        CommentAdapter adapter = new CommentAdapter(cListData2,DetailsActivity.this);
                                        listview.setAdapter(adapter);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            CommentInsertRequest commentInsertRequest = new CommentInsertRequest(video_index,  userName, desc,userName, responseListener1);
                            RequestQueue queue = Volley.newRequestQueue(DetailsActivity.this);
                            queue.add(commentInsertRequest);

                            if (response.startsWith("ï»¿")) {
                                response = response.substring(3);
                            }

                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject commentObject = jsonArray.getJSONObject(i);
                                String video_id = commentObject.getString("video_id");
                                String author = commentObject.getString("author");
                                String _index = commentObject.getString("_index");
                                String desc2 = commentObject.getString("desc");
                                String writetime = commentObject.getString("writetime");
                                String commentLike = commentObject.getString("commentLike");
                                String commentDisLike = commentObject.getString("commentDisLike");
                                String status = commentObject.getString("status");

                                CommentModel commentModel2 = new CommentModel(video_id,author,_index, desc2,writetime,commentLike,commentDisLike,status,1);
                                cListData2.add(commentModel2);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                final BestCommentRequest BestCommentRequest2 = new BestCommentRequest(video_index, userName,responseListenerBest3);
                RequestQueue queue3 = Volley.newRequestQueue(DetailsActivity.this);
                queue3.add(BestCommentRequest2);




                descText.setText(null);
                cListData2.clear();
            }
        });
    }

    public void update_likes(final int target){
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService=retrofit.create(RetrofitService.class);
        Call<JsonObject> call=retrofitService.updatelikes(userName,String.valueOf(video_index),String.valueOf(target));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Err", t.getMessage());
            }
        });
    }
    public void back_btn_pressed(View view) {
        finish();
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if (!wasRestored) {
            youTubePlayer.cueVideo(youtubeDataModel.getVideo_id());
        }
        mYoutubePlayer = youTubePlayer;
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onStopped() {

        }

        @Override
        public void onBuffering(boolean b) {

        }

        @Override
        public void onSeekTo(int i) {

        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {

        }

        @Override
        public void onLoaded(String s) {

        }

        @Override
        public void onAdStarted() {

        }

        @Override
        public void onVideoStarted() {

        }

        @Override
        public void onVideoEnded() {

        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {

        }
    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    public void share_btn_pressed(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String link = ("https://www.youtube.com/watch?v=" + youtubeDataModel.getVideo_id());
        // this is the text that will be shared
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, youtubeDataModel.getTitle()
                + "Share");

        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "share"));
    }


    private ProgressDialog pDialog;






    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int result2 = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return (result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED);
        }
        return false;
    }
}
