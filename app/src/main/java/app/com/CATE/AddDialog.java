package app.com.CATE;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.CATE.requests.VideoChugaRequest;
import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public class AddDialog extends Dialog implements View.OnClickListener {
    private static final int LAYOUT = R.layout.dialog_add;

    private Context context;

    private TextInputEditText titleEt;
    private TextInputEditText urlEt;
    private Spinner spinnerCate;
    private String kind_video, kind_thumbnail;
    private Button twitchRb;
    private Button youtubeRb;
    private Button confirmBt;
    ArrayList<String> arrayList;
    private String name;
    private String cate;
    ArrayAdapter<String> arrayAdapter;

    private Handler handler;

    public AddDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public AddDialog(Context context, String name) {
        super(context);
        this.context = context;
        this.name = name;
    }

    interface RetrofitAPI {
        @Headers({
                "Accept: application/vnd.twitchtv.v5+json",
                "Client-ID: ikngsfikq2ke5ub9hw5203pjekqp69"
        })
        @GET("kraken/clips/{ClipID}")
        Call<ClipVO> getClip(@Path("ClipID") String ClipID);
    }

    class ClipVO {
        Thumbnails thumbnails;

        class Thumbnails {
            String medium;

            String getMedium() {
                return medium;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        titleEt = findViewById(R.id.title_input);
        urlEt = findViewById(R.id.url_input);
        spinnerCate = findViewById(R.id.spinnerCate);

        confirmBt = findViewById(R.id.button_confirm);


        arrayList = new ArrayList<>();
        arrayList.add("롤");
        arrayList.add("재즈");
        arrayList.add("게임");
        arrayList.add("동물");
        arrayList.add("배그");
        arrayList.add("유머");
        arrayList.add("음악");

        arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item,
                arrayList);
        spinnerCate.setAdapter(arrayAdapter);
        spinnerCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cate = arrayList.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        출처: https://bottlecok.tistory.com/63 [잡캐의 IT 꿀팁]
        confirmBt.setOnClickListener(this);

        if (name != null) {
            titleEt.setText(name);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm:
                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");


                            //서버에서 보내준 값이 true이면?
                            if (success) {

                                Toast.makeText(context, "비디오 추가하셨습니다.", Toast.LENGTH_SHORT).show();

                            } else {//충전 실패시
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("추가에 실패하셨습니다.")
                                        .setNegativeButton("retry", null)
                                        .create()
                                        .show();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //
                if (urlEt.getText().toString().indexOf("www.youtube.com") > 0) {
                    kind_video = "YOUTUBE";
                    kind_thumbnail = "https://i.ytimg.com/vi/" + urlEt.getText().toString().substring(urlEt.getText().toString().indexOf("=") + 1) + "/mqdefault.jpg";

                    VideoChugaRequest VideoChugaRequest = new VideoChugaRequest(titleEt.getText().toString(), urlEt.getText().toString(),
                            cate, kind_video, MainActivity.strName, kind_thumbnail, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(VideoChugaRequest);
                    cancel();

                } else if (urlEt.getText().toString().indexOf("clips.twitch.tv") > 0) {

                    kind_video = "TWITCH";

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.twitch.tv/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

                    String[] split = urlEt.getText().toString().split("/");
                    kind_thumbnail = split[split.length - 1];

                    retrofitAPI.getClip(kind_thumbnail).enqueue(new Callback<ClipVO>() {
                        @Override
                        public void onResponse(Call<ClipVO> call, retrofit2.Response<ClipVO> response) {
                            kind_thumbnail = response.body().thumbnails.getMedium();
                            Log.d("썸네일 주소", kind_thumbnail);
                            handler.sendEmptyMessage(0);
                        }

                        @Override
                        public void onFailure(Call<ClipVO> call, Throwable t) {

                        }
                    });

                } else {
                    Toast.makeText(context, "동영상 추가 실패하였습니다.", Toast.LENGTH_LONG).show();
                    break;
                }

                handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        VideoChugaRequest VideoChugaRequest = new VideoChugaRequest(titleEt.getText().toString(), urlEt.getText().toString(),
                                cate, kind_video, MainActivity.strName, kind_thumbnail, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(VideoChugaRequest);
                        cancel();
                    }
                };
                break;
        }
    }
}