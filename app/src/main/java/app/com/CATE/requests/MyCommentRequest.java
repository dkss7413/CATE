package app.com.CATE.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MyCommentRequest extends StringRequest {
    final static private String URL = "http://ghkdua1829.dothome.co.kr/fow/fow_getMyComment.php";
    private Map<String, String> parameters;

    public MyCommentRequest(String username, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("username", username + "");
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
