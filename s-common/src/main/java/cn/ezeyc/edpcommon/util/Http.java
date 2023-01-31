package cn.ezeyc.edpcommon.util;

import cn.ezeyc.edpcommon.pojo.ResultBody;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Http {

    public static String sendGet(String url, String param){
        return sendGet(url,null,param);
    }
    public static String sendGet(String url,Map<String,Object> headers,  JSONObject param){
        String p="?";
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            p+=entry.getKey()+"="+entry.getValue()+"&";
        }
        p=p.substring(0,p.length()-1);
        return sendGet(url,headers,p);
    }
    public static String sendGet(String url,  JSONObject param){
        String p="?";
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            p+=entry.getKey()+"="+entry.getValue()+"&";
        }
        p=p.substring(0,p.length()-1);
        return sendGet(url,null,p);
    }
    public static String sendPost(String url,Map<String,Object> headers, JSONObject param){
        return  sendPost(url,headers,param,true);
    }
    public static String sendPost(String url, JSONObject param){
        return  sendPost(url,null,param,true);
    }
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数,请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url,Map<String,Object> headers,  String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            System.out.println(urlNameString);
            trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            if(headers!=null){
                for (Map.Entry item : headers.entrySet()) {
                    connection.setRequestProperty(item.getKey().toString(),item.getValue().toString());//设置header
                }
            }
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数,请求参数应该是 json格式 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url,Map<String,Object> headers, JSONObject param , Boolean isJson) {

//        RestTemplate client = new RestTemplate();
//
//        JSONObject json = client.postForEntity(url, param, JSONObject.class).getBody();
//        return  json.toJSONString();
        BufferedReader in = null;
        StringBuffer  result = new StringBuffer();
        HttpURLConnection conn=null;
        OutputStream out=null;
        BufferedReader br=null;
        try {
            trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
             conn = (HttpURLConnection)realUrl.openConnection();
             if(headers!=null){
                 for (Map.Entry item : headers.entrySet()) {
                     conn.setRequestProperty(item.getKey().toString(),item.getValue().toString());//设置header
                 }
             }
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            if(isJson){
                conn.setRequestProperty("Content-Type","application/json");
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //开始连接
            conn.connect();

            // 传递参数  流的方式
            out=conn.getOutputStream();
            out.write(param.toString().getBytes());
            out.flush();

            // 读取数据
            br=new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line=null;
            while (null != (line=br.readLine())){
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * post请求并得到返回结果
     *
     * @param requestUrl
     * @param requestMethod
     * @param output
     * @return
     */
    public static String httpsRequest(String requestUrl, String requestMethod, String output) {
        try {
            trustAllHttpsCertificates();
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            URL url = new URL(requestUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod(requestMethod);
            if (null != output) {
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(output.getBytes(StandardCharsets.UTF_8));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            connection.disconnect();
            return buffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";
    }

    public static void returnJson(HttpServletResponse response, ResultBody resultBody) {
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        try {
            writer = response.getWriter();
            writer.print(JSON.toJSON(resultBody));
        } catch (IOException e) {
        } finally {
            if (writer != null){
                writer.close();
            }
        }
    }
    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    static class miTM implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }
    }
}