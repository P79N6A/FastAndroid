package com.apache.fastandroid.artemis.support.http;

import com.alibaba.fastjson.JSON;
import com.tesla.framework.common.setting.Setting;
import com.tesla.framework.common.util.log.NLog;
import com.tesla.framework.network.http.HttpConfig;
import com.tesla.framework.network.http.IHttpUtility;
import com.tesla.framework.network.http.Params;
import com.tesla.framework.network.http.ParamsUtil;
import com.tesla.framework.network.task.TaskException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 01370340 on 2017/11/6.
 * 基于HttpUrlConnection的实现
 *
 *
 */

public class HttpUrlConnectionUtility implements IHttpUtility {
    @Override
    public <T> T doGet(HttpConfig config, Setting action, Params urlParams, Class<T> responseCls) throws TaskException {
        HttpURLConnection urlConnection = null;
        //发送GET请求
        try {
            String requestUrl = (config.baseUrl + action.getValue() + (urlParams == null || urlParams.size() == 0? "" : "?" + ParamsUtil.encodeToURLParams(urlParams))).replaceAll(" ", "");
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            //HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
            urlConnection.setRequestMethod("GET");
            //HttpURLConnection默认也支持从服务端读取结果流，所以下面的setDoInput也可以省略
            urlConnection.setDoInput(true);
            //用setRequestProperty方法设置一个自定义的请求头:action，由于后端判断

            urlConnection.setRequestProperty("action", "NETWORK_GET");


            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);


            // add header
            if (config.headerMap.size() > 0) {
                Set<String> keySet = config.headerMap.keySet();
                for (String key : keySet) {

                    urlConnection.setRequestProperty(key,config.headerMap.get(key));
                }
            }
            //禁用网络缓存
            urlConnection.setUseCaches(false);
            //获取请求头
            String requestHeader = getReqeustHeader(urlConnection);
            //在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
            //conn.connect()方法不必显式调用，当调用conn.getInputStream()方法时内部也会自动调用connect方法

            //调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
            InputStream is = urlConnection.getInputStream();
            //请求成功
            if (urlConnection.getResponseCode() == 200){
                //将InputStream转换成byte数组,getBytesByInputStream会关闭输入流
                byte[] responseBody = getBytesByInputStream(is);

                String responseStr = new String(responseBody);

                return parseResponse(responseStr, responseCls);
            }else {
                byte[] responseBody = getBytesByInputStream(is);
                String responseStr = new String(responseBody);
                TaskException.checkResponse(responseStr);
                throw new TaskException(TaskException.TaskError.timeout.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new TaskException("");
        }catch (TaskException e){
            throw e;
        }catch (Exception e){
            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
        }
    }

    @Override
    public <T> T doPost(HttpConfig config, Setting action, Params urlParams, Params bodyParams, Object requestObj,
                        Class<T> responseCls) throws TaskException {
        return null;
    }

    @Override
    public <T> T doPostFiles(HttpConfig config, Setting action, Params urlParams, Params bodyParams, MultipartFile[] files, Class<T> responseCls) throws TaskException {
        return null;
    }

    protected <T> T parseResponse(String resultStr, Class<T> responseCls) throws TaskException {
        NLog.w("BizLogic",  "parseResponse = %s", resultStr);
        if (responseCls.getSimpleName().equals("String"))
            return (T) resultStr;

        T result = JSON.parseObject(resultStr, responseCls);
        return result;
    }




    //读取请求头
    private String getReqeustHeader(HttpURLConnection conn) {
        //https://github.com/square/okhttp/blob/master/okhttp-urlconnection/src/main/java/okhttp3/internal/huc/HttpURLConnectionImpl.java#L236
        Map<String, List<String>> requestHeaderMap = conn.getRequestProperties();
        Iterator<String> requestHeaderIterator = requestHeaderMap.keySet().iterator();
        StringBuilder sbRequestHeader = new StringBuilder();
        while (requestHeaderIterator.hasNext()) {
            String requestHeaderKey = requestHeaderIterator.next();
            String requestHeaderValue = conn.getRequestProperty(requestHeaderKey);
            sbRequestHeader.append(requestHeaderKey);
            sbRequestHeader.append(":");
            sbRequestHeader.append(requestHeaderValue);
            sbRequestHeader.append("\n");
        }
        return sbRequestHeader.toString();
    }

    //读取响应头
    private String getResponseHeader(HttpURLConnection conn) {
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        StringBuilder sbResponseHeader = new StringBuilder();
        for(int i = 0; i < size; i++){
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            String responseHeaderValue = conn.getHeaderField(i);
            sbResponseHeader.append(responseHeaderKey);
            sbResponseHeader.append(":");
            sbResponseHeader.append(responseHeaderValue);
            sbResponseHeader.append("\n");
        }
        return sbResponseHeader.toString();
    }

    //根据字节数组构建UTF-8字符串
    private String getStringByBytes(byte[] bytes) {
        String str = "";
        try {
            str = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    //从InputStream中读取数据，转换成byte数组，最后关闭InputStream
    private byte[] getBytesByInputStream(InputStream is) {
        byte[] bytes = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        try {
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }


}
