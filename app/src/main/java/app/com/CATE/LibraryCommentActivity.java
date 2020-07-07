package app.com.CATE;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.CATE.adapters.CommentAdapter;
import app.com.CATE.models.CommentModel;
import app.com.CATE.requests.CommentRequest;
import app.com.CATE.requests.MyCommentRequest;
import app.com.youtubeapiv3.R;

public class LibraryCommentActivity extends AppCompatActivity {

    //선언부
   int size;
   ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_comment);

        //if s equals 내가 쓴 댓글
        listview = findViewById(R.id.list_comment);

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("reuslt", response);

                try {
                    Log.d("테스트", response);

                    if (response.startsWith("ï»¿")) {
                        response = response.substring(3);
                    }
                    ArrayList<CommentModel> cListData = new ArrayList<>();
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

                        CommentModel commentModel = new CommentModel(video_id, author,_index, desc,writetime,commentLike,commentDisLike,status,0);
                        cListData.add(commentModel);
                    }
                    if(cListData.isEmpty()) size = 0;
                    else size = cListData.size();

                    CommentAdapter adapter = new CommentAdapter(cListData, LibraryCommentActivity.this);
                    listview.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        final MyCommentRequest commentRequest = new MyCommentRequest(MainActivity.strName,responseListener);
        RequestQueue queue = Volley.newRequestQueue(LibraryCommentActivity.this);
        queue.add(commentRequest);

    }


}