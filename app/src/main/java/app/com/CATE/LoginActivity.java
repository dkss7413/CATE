package app.com.CATE;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ApiErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import org.json.JSONException;
import org.json.JSONObject;

import app.com.CATE.interfaces.RetrofitService;
import app.com.CATE.requests.LoginRequest_KAKAO;

import app.com.youtubeapiv3.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button BtnSignUp;
    CheckBox AutoLogincheck;
    String finalresult;
    public static Context mContext;
    public static EditText idText;
    public static EditText passwordText;
    private SessionCallback sessionCallback;
    public static SharedPreferences loginInformation; //자동로그인 추가

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        loginInformation =getSharedPreferences("setting",MODE_PRIVATE);
        sessionCallback = new SessionCallback(); //SessionCallback 초기화
        Session.getCurrentSession().addCallback(sessionCallback); //현재 세션에 콜백 붙임
//        Session.getCurrentSession().checkAndImplicitOpen(); //자동 로그인

        idText = findViewById(R.id.idText);
        passwordText = findViewById(R.id.passwordText);
        final Button loginbtn = findViewById(R.id.buttonLogin);
        BtnSignUp = findViewById(R.id.buttonSignUp);
        AutoLogincheck=findViewById(R.id.AutoLogInCheck);
        if(!loginInformation.getString("id","").equalsIgnoreCase("")){
            LoginReq(loginInformation.getString("id",null),loginInformation.getString("password",null));
        }

        BtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, SignupPage.class);
                registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginReq(idText.getText().toString(),passwordText.getText().toString());
            }
        });

    }

    public void LoginReq(final String id,final String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<JsonObject> call = retrofitService.Login_cate(id,password);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                boolean success = jsonObject.get("success").getAsBoolean();

                if (success) {

                    Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                    if(AutoLogincheck.isChecked()) {
                        SharedPreferences.Editor editor = loginInformation.edit();
                        editor.putString("id", id);
                        editor.putString("password", password);
                        editor.commit();
                    }
                    String userID = jsonObject.get("userID").getAsString();
                    String userName = jsonObject.get("userName").getAsString();
                    String Api = jsonObject.get("Api").getAsString();


                    //로그인에 성공했으므로 MenuPage로 넘어감
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userID", userID);
                    intent.putExtra("userName", userName);
                    intent.putExtra("Api", Api);

                    LoginActivity.this.startActivity(intent);
                    finish();

                } else {//로그인 실패시

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("로그인에 실패하셨습니다.")
                            .setNegativeButton("retry", null)
                            .create()
                            .show();


                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Err", t.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    int result = errorResult.getErrorCode();

                    if(result == ApiErrorCode.CLIENT_ERROR_CODE) {
                        Toast.makeText(getApplicationContext(), "네트워크 연결이 불안정합니다. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),"로그인 도중 오류가 발생했습니다: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Toast.makeText(getApplicationContext(),"세션이 닫혔습니다. 다시 시도해 주세요: "+errorResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(final MeV2Response result) {

                    Response.Listener<String> responseListener2 = new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                                JSONObject jsonResponse2 = new JSONObject(response);

                                boolean success = jsonResponse2.getBoolean("success");


                                //서버에서 보내준 값이 true이면?
                                if (success) {

                                    Toast.makeText(getApplicationContext(), "로그인에 성공하셨습니다.", Toast.LENGTH_SHORT).show();



                                    //로그인에 성공했으므로 MenuPage로 넘어감
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("userID", jsonResponse2.getString("userID"));
                                    intent.putExtra("userName", jsonResponse2.getString("userName"));
                                    intent.putExtra("Api", "KAKAO");

                                    LoginActivity.this.startActivity(intent);
                                    finish();

                                } else {//로그인 실패시 회원가입을 실시한다.

                                    Intent intent = new Intent(LoginActivity.this, SignupPage_API.class);
                                    intent.putExtra("userID", String.valueOf(result.getId()));

                                    LoginActivity.this.startActivity(intent);
                                    finish();


                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    LoginRequest_KAKAO LoginRequest_KAKAO = new LoginRequest_KAKAO(String.valueOf(result.getId()), "KAKAO", responseListener2);
                    RequestQueue queue2 = Volley.newRequestQueue(LoginActivity.this);
                    queue2.add(LoginRequest_KAKAO);

                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) {
            Toast.makeText(getApplicationContext(), "로그인 도중 오류가 발생했습니다. 인터넷 연결을 확인해주세요: "+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}