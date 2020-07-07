package app.com.CATE.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CommentInsertRequest extends StringRequest {
    final static private String URL = "http://ghkdua1829.dothome.co.kr/fow/fow_insertComment.php";
    private Map<String, String> parameters;

    public CommentInsertRequest(int videoID,  String author, String desc,String username, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("videoID", videoID + "");
        parameters.put("author", author);
        parameters.put("desc", desc);
        parameters.put("username", username);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
