package mi.simplenet.org.request;

import org.json.JSONException;
import org.json.JSONObject;

import mi.simplenet.org.base.Request;
import mi.simplenet.org.base.Response;

/**
 * 返回的数据类型为Json的请求, Json对应的对象类型为JSONObject
 * Created by JW.Xuan on 2016/12/5 16:22.
 * 邮箱：mifind@sina.com
 */
public class JsonRequest extends Request<JSONObject> {

    public JsonRequest(HttpMethod method, String url, RequestListener<JSONObject> listener) {
        super(method, url, listener);
    }


    /**
     * 将Response的结果转换为JSONObject
     */
    @Override
    public JSONObject parseResponse(Response response) {
        String jsonString = new String(response.getRawData());
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
