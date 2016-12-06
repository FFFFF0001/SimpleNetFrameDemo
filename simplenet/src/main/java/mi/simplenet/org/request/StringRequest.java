package mi.simplenet.org.request;

import mi.simplenet.org.base.Request;
import mi.simplenet.org.base.Response;

/**
 * 返回的数据类型为String的请求
 * Created by JW.Xuan on 2016/12/5 14:06.
 * 邮箱：mifind@sina.com
 */
public class StringRequest extends Request<String> {

    public StringRequest(Request.HttpMethod method, String url, RequestListener<String> listener) {
        super(method, url, listener);
    }

    @Override
    public String parseResponse(Response response) {
        return new String(response.getRawData());
    }

}
