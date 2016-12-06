package mi.simplenet.org.base;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求类，注意GET和DELETE不能传递请求参数，因为其请求的性质所致，用户可以将参数构建到url后传递进来到Request中。
 *
 * @param <T> T为请求返回的数据类型
 *            <p/>
 *            Created by JW.Xuan on 2016/12/5 14:06.
 *            邮箱：mifind@sina.com
 */
public abstract class Request<T> implements Comparable<Request<T>> {

    /**
     * http请求方法枚举，这里我们只有GET,POST,PUT,DELETE四种
     */
    public static enum HttpMethod {
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private String mHttpMethod = "";

        private HttpMethod(String method) {
            mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    /**
     * 优先级枚举
     */
    public static enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE;
    }

    /**
     * POST和PUT的默认编码方式
     */
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    /**
     * Default Content-type
     */
    public final static String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * 请求序列号
     */
    protected int mSerialNum = 0;
    /**
     * 优先级默认设置Normal
     */
    protected Priority mPriority = Priority.NORMAL;
    /**
     * 是否取消改请求
     */
    protected boolean isCancel = false;
    /**
     * 该请求是否应该缓存
     */
    protected boolean mShouldCache = true;
    /**
     * 请求Listener
     */
    protected RequestListener<T> mRequestListener;

    /**
     * 请求的Url
     */
    private String mUrl = "";
    /**
     * 请求的方法
     */
    HttpMethod mHttpMethod = HttpMethod.GET;
    /**
     * 请求的header
     */
    private Map<String, String> mHeaders = new HashMap<>();
    /**
     * 请求参数
     */
    private Map<String, String> mBodyParams = new HashMap<>();

    /**
     * 构造
     *
     * @param method
     * @param url
     * @param listener
     */
    public Request(HttpMethod method, String url, RequestListener<T> listener) {
        mHttpMethod = method;
        mUrl = url;
        mRequestListener = listener;
    }

    /**
     * 从原生的网络请求中解析结果，子类复写
     *
     * @param response
     * @return
     */
    public abstract T parseResponse(Response response);

    public final void deliveryResponse(Response response) {
        // 解析得到请求结果
        T result = parseResponse(response);
        if (mRequestListener != null) {
            int stCode = response != null ? response.getStatusCode() : -1;
            String msg = response != null ? response.getMessage() : "unknown error";
            mRequestListener.onComplete(stCode, result, msg);
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public int getSerialNum() {
        return mSerialNum;
    }

    public void setSerialNum(int serialNum) {
        mSerialNum = serialNum;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }

    protected String getParamsEncoding() {
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mBodyParams;
    }
    public boolean isHttps() {
        return mUrl.startsWith("https");
    }

    /**
     * 该请求是否应该缓存
     *
     * @param shouldCache
     */
    public void setShouldCache(boolean shouldCache) {
        this.mShouldCache = shouldCache;
    }

    public boolean shouldCache() {
        return mShouldCache;
    }


    public void cancel() {
        isCancel = true;
    }

    public boolean isCanceled() {
        return isCancel;
    }

    /**
     * 返回POST或者PUT请求时的Body参数字节数组
     *
     * @return
     */
    public byte[] getBody() {
        Map<String, String> params = getParams();
        if (params != null && params.size() > 0) {
            return encodeParameters(params, getParamsEncoding());
        }
        return null;
    }

    /**
     * 将参数转换为Url编码的参数串
     */
    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }


    /**
     * 因故对于请求的排序处理，根据优先级和加入到队列的序号进行排序
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(Request<T> another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        //如果优先级相等，那么按照添加队列的序列号顺序来执行
        return myPriority.equals(another) ? this.getSerialNum() - another.getSerialNum()
                : myPriority.ordinal() - anotherPriority.ordinal();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mHeaders == null) ? 0 : mHeaders.hashCode());
        result = prime * result + ((mHttpMethod == null) ? 0 : mHttpMethod.hashCode());
        result = prime * result + ((mBodyParams == null) ? 0 : mBodyParams.hashCode());
        result = prime * result + ((mPriority == null) ? 0 : mPriority.hashCode());
        result = prime * result + (mShouldCache ? 1231 : 1237);
        result = prime * result + ((mUrl == null) ? 0 : mUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Request<?> other = (Request<?>) obj;
        if (mHeaders == null) {
            if (other.mHeaders != null)
                return false;
        } else if (!mHeaders.equals(other.mHeaders))
            return false;
        if (mHttpMethod != other.mHttpMethod)
            return false;
        if (mBodyParams == null) {
            if (other.mBodyParams != null)
                return false;
        } else if (!mBodyParams.equals(other.mBodyParams))
            return false;
        if (mPriority != other.mPriority)
            return false;
        if (mShouldCache != other.mShouldCache)
            return false;
        if (mUrl == null) {
            if (other.mUrl != null)
                return false;
        } else if (!mUrl.equals(other.mUrl))
            return false;
        return true;
    }


    /**
     * 网络请求Listener，会被执行在UI线程
     *
     * @param <T> 请求的response类型
     */
    public static interface RequestListener<T> {
        /**
         * 请求完成的回调
         *
         * @param stCode
         * @param response
         * @param errMsg
         */
        public void onComplete(int stCode, T response, String errMsg);
    }
}
