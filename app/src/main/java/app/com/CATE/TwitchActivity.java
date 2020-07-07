package app.com.CATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
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
import app.com.CATE.models.YoutubeDataModel;
import app.com.CATE.requests.BestCommentRequest;
import app.com.CATE.requests.CommentInsertRequest;
import app.com.CATE.requests.CommentRequest;
import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TwitchActivity extends AppCompatActivity {
    private YoutubeDataModel youtubeDataModel = null;
    WebView web;
    TextView title;
    ListView listview;
    String userID = "";
    String videoID = "";
    public static int video_index;
    int u_v_status, likes, dislikes;
    public static String userName = "";
    TextView textViewName, countLike, countDisLike;
    ImageView imageButtonLike, imageButtonDisLike, declaration_posting;
    final ArrayList<CommentModel> cListData = new ArrayList<>();
    final ArrayList<CommentModel> cListData2 = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitch);
        web = findViewById(R.id.twitchPlayer);

        userName = MainActivity.strName;

        Intent intent = getIntent();
        youtubeDataModel = intent.getParcelableExtra(YoutubeDataModel.class.toString());
        videoID = youtubeDataModel.getVideo_id();
        video_index = youtubeDataModel.getVideo_index();
        likes = youtubeDataModel.getLikes();
        dislikes = youtubeDataModel.getDislikes();
        u_v_status = intent.getIntExtra("u_v_status",0);

        textViewName = findViewById(R.id.textViewName);

        TextView ss = findViewById(R.id.textViewDate);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date time = new Date();

        String time1 = format1.format(time);
        ss.setText(time1);
        textViewName.setText(youtubeDataModel.getTitle());

        listview = findViewById(R.id.commentList);

        imageButtonLike = findViewById(R.id.imageButtonLike);
        imageButtonDisLike = findViewById(R.id.imageButtonDisLike);
        declaration_posting = findViewById(R.id.declaration_posting);

        declaration_posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edittext = new EditText(TwitchActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(TwitchActivity.this);
                builder.setTitle("신고하기");
                builder.setMessage("신고 사유를 적어주세요.");
                builder.setView(edittext);
                builder.setPositiveButton("입력",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog pd = ProgressDialog.show(TwitchActivity.this, "", "신고 내용을 보내는 중입니다.");
                                Thread t = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            GMailSender gMailSender = new GMailSender("ghkdua059@gmail.com", "6013861z!");
                                            gMailSender.sendMail("게시글 번호 : " + video_index + " 를(을) 신고합니다.",
                                                    "신고 이유 : " + edittext.getText().toString(), "ghkdua1829@naver.com");
                                            Looper.prepare();
                                            Toast.makeText(TwitchActivity.this, "신고가 접수되었습니다.", Toast.LENGTH_SHORT).show();
                                            pd.dismiss();
                                        } catch (SendFailedException e) {
                                            Toast.makeText(TwitchActivity.this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                                        } catch (MessagingException e) {
                                            Toast.makeText(TwitchActivity.this, "인터넷 연결을 확인해주십시오", Toast.LENGTH_SHORT).show();
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
        if (u_v_status == 1) {
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);
            imageButtonLike.setTag(R.drawable.ic_thumb_up_selected_24px);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
        } else if (u_v_status == 2) {
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
            imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
        } else {
            imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
            imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
            imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
            imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
        }

        countLike = findViewById(R.id.countLike);
        countDisLike = findViewById(R.id.countDisLike);

        countLike.setText(String.valueOf(likes));
        countDisLike.setText(String.valueOf(dislikes));
        imageButtonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_selected_24px)) {     //좋아요 취소
                    update_likes(5);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) - 1));
                } else if (imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_24px) && imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_selected_24px)) {
                    update_likes(3);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) + 1));
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString()) - 1));
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_selected_24px);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
                } else {
                    update_likes(1);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_selected_24px);      //좋아요 누르기
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_selected_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) + 1));
                }
            }
        });
        imageButtonDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_selected_24px)) {    //싫어요 취소
                    update_likes(6);
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_24px);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString()) - 1));
                } else if (imageButtonDisLike.getTag().equals(R.drawable.ic_thumb_down_24px) && imageButtonLike.getTag().equals(R.drawable.ic_thumb_up_selected_24px)) {
                    update_likes(4);
                    imageButtonLike.setImageResource(R.drawable.ic_thumb_up_24px);
                    countLike.setText(String.valueOf(Integer.parseInt(countLike.getText().toString()) - 1));
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString()) + 1));
                    imageButtonLike.setTag(R.drawable.ic_thumb_up_24px);
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
                } else {             //i가 1일때 싫어요 클릭이 안된 상태
                    update_likes(2);
                    imageButtonDisLike.setImageResource(R.drawable.ic_thumb_down_selected_24px);      //싫어요 누르기
                    imageButtonDisLike.setTag(R.drawable.ic_thumb_down_selected_24px);
                    countDisLike.setText(String.valueOf(Integer.parseInt(countDisLike.getText().toString()) + 1));
                }
            }
        });


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

                                CommentAdapter adapter = new CommentAdapter(cListData,TwitchActivity.this);
                                listview.setAdapter(adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    final CommentRequest commentRequest = new CommentRequest(video_index, userName,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(TwitchActivity.this);
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
        RequestQueue queue2 = Volley.newRequestQueue(TwitchActivity.this);
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

                                        CommentAdapter adapter = new CommentAdapter(cListData2,TwitchActivity.this);
                                        listview.setAdapter(adapter);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };

                            CommentInsertRequest commentInsertRequest = new CommentInsertRequest(video_index,  userName, desc,userName, responseListener1);
                            RequestQueue queue = Volley.newRequestQueue(TwitchActivity.this);
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
                RequestQueue queue3 = Volley.newRequestQueue(TwitchActivity.this);
                queue3.add(BestCommentRequest2);




                descText.setText(null);
                cListData2.clear();
            }
        });


        web.getSettings().setJavaScriptEnabled(true);
        web.setWebChromeClient(new FullscreenableChromeClient(TwitchActivity.this));                //전체화면
        web.setWebViewClient(new WebClient());                                                       //다른 웹브라우저로 여는 것을 방지

//        web.getSettings().setPluginState(WebSettings.PluginState.ON);             //플러그인 설정
//        web.getSettings().setSupportMultipleWindows(true);                        //웹뷰에서 여러 개의 윈도우를 사용가능하게함
//        web.setLayerType(View.LAYER_TYPE_HARDWARE, null);                         //뷰 가속 - 영상 재생 가능하게 설정
//        if (android.os.Build.VERSION.SDK_INT >= 11) {                             //GPU를 가속
//            getWindow().addFlags(16777216);
//        }

//        긴 트위치 영상
//        final String html1 = "<iframe frameborder=\"0\"\n" +
//                "scrolling=\"no\"\n" +
//                "id=\"chat_embed\"\n" +
//                "allowfullscreen=\"true\"\n" +
//                "src=\"https://player.twitch.tv/?autoplay=false&video=v";
//        final String html2 = "\"\n" +
//                "height=\"200\"\n" +
//                "width=\"400\">\n" +
//                "</iframe>";

        //트위치 클립
        final String html1 = "<iframe frameborder=\"0\"\n" +
                "scrolling=\"no\"\n" +
                "id=\"chat_embed\"\n" +
                "allowfullscreen=\"true\"\n" +
                "src=\"https://clips.twitch.tv/embed?clip=";
        final String html2 = "&tt_medium=clips_api&tt_content=embed\"\n" +
                "height=\"200\"\n" +
                "width=\"400\">\n" +
                "</iframe>";

        web.loadData(html1 + videoID + html2, "text/html", null);
    }

    public void update_likes(final int target) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<JsonObject> call = retrofitService.updatelikes(userName, String.valueOf(video_index), String.valueOf(target));
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
}

class WebClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }
}

class FullscreenableChromeClient extends WebChromeClient {
    private Activity mActivity = null;

    private View mCustomView;
    private CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private FrameLayout mFullscreenContainer;
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    public FullscreenableChromeClient(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }

            mOriginalOrientation = mActivity.getRequestedOrientation();
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            mFullscreenContainer = new FullscreenHolder(mActivity);
            mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
            mCustomView = view;
            setFullscreen(true);
            mCustomViewCallback = callback;
//          mActivity.setRequestedOrientation(requestedOrientation);

        }

        super.onShowCustomView(view, callback);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        this.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }

        setFullscreen(false);
        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCallback.onCustomViewHidden();
        mActivity.setRequestedOrientation(mOriginalOrientation);

    }

    private void setFullscreen(boolean enabled) {

        Window win = mActivity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);
    }

    private static class FullscreenHolder extends FrameLayout {
        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
}