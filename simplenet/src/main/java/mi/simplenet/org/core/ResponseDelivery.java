package mi.simplenet.org.core;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

import mi.simplenet.org.base.Request;
import mi.simplenet.org.base.Response;

/**
 * Created by JW.Xuan on 2016/12/5 18:16.
 * 邮箱：mifind@sina.com
 */
public class ResponseDelivery implements Executor {

    /**
     * 主线程的hander
     */
    Handler mResponseHandler = new Handler(Looper.getMainLooper());

    /**
     * 处理请求结果,将其执行在UI线程
     *
     * @param request
     * @param response
     */
    public void deliveryResponse(final Request<?> request, final Response response) {
        Runnable respRunnable = new Runnable() {

            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };

        execute(respRunnable);
    }

    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }

}
