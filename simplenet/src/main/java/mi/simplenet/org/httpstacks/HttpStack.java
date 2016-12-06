package mi.simplenet.org.httpstacks;

import mi.simplenet.org.base.Request;
import mi.simplenet.org.base.Response;

/**
 * 执行网络请求的接口
 * Created by JW.Xuan on 2016/12/5 18:17.
 * 邮箱：mifind@sina.com
 */
public interface HttpStack {
    /**
     * 执行Http请求
     *
     * @param request 待执行的请求
     * @return
     */
    public Response performRequest(Request<?> request);
}