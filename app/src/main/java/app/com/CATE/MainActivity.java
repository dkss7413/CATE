package app.com.CATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;
import java.util.Objects;

import app.com.CATE.models.YoutubeDataModel;
import app.com.youtubeapiv3.R;

public class MainActivity extends AppCompatActivity {
    public static String strID, strName,Api;
    public ViewPager viewPager = null;
    private Toolbar toolbar = null;
    public String category;

    TextView txtResult;
    public ArrayList<YoutubeDataModel> listData = new ArrayList<>();

    public static Intent likeVideoIntent;
    public static Intent LibraryCommentIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        category = intent.getStringExtra("Category");
        strID = intent.getStringExtra("userID");
        strName = intent.getStringExtra("userName");
        Api = intent.getStringExtra("Api");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);  // 왼쪽 버튼 사용 여부 true
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_play_arrow_24px); // 왼쪽 버튼 이미지 설정
//        actionBar.setDisplayShowTitleEnabled(false);    // 타이틀 안보이게 하기

        likeVideoIntent = new Intent(MainActivity.this, LibraryLikeVideoActivity.class);
        LibraryCommentIntent = new Intent(MainActivity.this, LibraryCommentActivity.class);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        //setting the tabs title
        tabLayout.addTab(tabLayout.newTab().setText("Home"));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Category"));
        tabLayout.addTab(tabLayout.newTab().setText("Library"));

        //setup the view pager
        final PagerAdapter adapter = new app.com.CATE.adapters.PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);      // main_menu 메뉴를 toolbar 메뉴 버튼으로 설정
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 클릭된 메뉴 아이템의 아이디 마다 switch 구절로 클릭시 동작을 설정한다.
        switch (item.getItemId()) {
            case android.R.id.home:    // 검색 버튼
                Snackbar.make(toolbar, "Menu pressed", Snackbar.LENGTH_SHORT).show();
                return true;

            case R.id.menu_add: // 추가 버튼
                Snackbar.make(toolbar, "Search menu pressed", Snackbar.LENGTH_SHORT).show();
                AddDialog dialog = new AddDialog(this);
                dialog.show();
                return true;

            case R.id.menu_account: // 계정 버튼
//                Snackbar.make(toolbar, "Account menu pressed", Snackbar.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("내 정보")
                        .setMessage("\n" + "아이디 : " +  strID + "\n" + "닉네임 : " + strName);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            case R.id.menu_logout:// 로그아웃 버튼
                Snackbar.make(toolbar, "Logout menu pressed", Snackbar.LENGTH_SHORT).show();
                SharedPreferences.Editor editor=LoginActivity.loginInformation.edit();
                editor.clear();
                editor.apply();
                    UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                        @Override
                        public void onCompleteLogout() {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //팝업 엑티비티 종료
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
                txtResult.setText(result);
            }
        }
    }
}
