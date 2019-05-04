package com.example.myapplication;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;


public class HttpAssist {
    private static final String TAG = "uploadFile";
    private static final int TIME_OUT = 10 * 10000000; // 超时时间
    private static final String CHARSET = "utf-8"; // 设置编码
    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";

    public static String uploadFile(File file, String id, String attenId) {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String RequestURL = "http://39.97.105.151:8000/stu_upload/";
        try {
            Log.d("笑嘻嘻", "1");
            URL url = new URL(RequestURL);
            Log.d("笑嘻嘻", "2");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d("笑嘻嘻", "3");
            conn.setReadTimeout(TIME_OUT);
            Log.d("笑嘻嘻", "4");
            conn.setConnectTimeout(TIME_OUT);
            Log.d("笑嘻嘻", "5");
            conn.setDoInput(true); // 允许输入流
            Log.d("笑嘻嘻", "6");
            conn.setDoOutput(true); // 允许输出流
            Log.d("笑嘻嘻", "7");
            conn.setUseCaches(false); // 不允许使用缓存
            Log.d("笑嘻嘻", "8");
            conn.setRequestMethod("POST"); // 请求方式
            Log.d("笑嘻嘻", "9");
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            Log.d("笑嘻嘻", "10");
            conn.setRequestProperty("connection", "keep-alive");
            Log.d("笑嘻嘻", "11");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            Log.d("笑嘻嘻", "12");
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                Log.d("笑嘻嘻", "pp");
                //conn.setDoOutput(true);
                OutputStream outputSteam = conn.getOutputStream();
                Log.d("笑嘻嘻", "13");
                DataOutputStream dos = new DataOutputStream(outputSteam);
                Log.d("笑嘻嘻", "14");
                StringBuffer sb = new StringBuffer();
                Log.d("笑嘻嘻", "15");
                sb.append(PREFIX);
                Log.d("笑嘻嘻", "16");
                sb.append(BOUNDARY);
                Log.d("笑嘻嘻", "17");
                sb.append(LINE_END);
                Log.d("笑嘻嘻", "18");
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"id\""+LINE_END);
                sb.append(LINE_END);
                sb.append(id+LINE_END);
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"atten_id\""+LINE_END);
                sb.append(LINE_END);
                sb.append(attenId+LINE_END);
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                Log.d("笑嘻嘻", "19");
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                Log.d("笑嘻嘻", "20");
                sb.append(LINE_END);
                Log.d("笑嘻嘻", "21");
                dos.write(sb.toString().getBytes());
                Log.d("笑嘻嘻", "22");
                InputStream is = new FileInputStream(file);
                Log.d("笑嘻嘻", "23");
                byte[] bytes = new byte[1024];
                Log.d("笑嘻嘻", "24");
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    Log.d("笑嘻嘻", "25");
                    dos.write(bytes, 0, len);
                }
                is.close();
                Log.d("笑嘻嘻", "26");
                dos.write(LINE_END.getBytes());
                Log.d("笑嘻嘻", "27");
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                Log.d("笑嘻嘻", "28");
                dos.write(end_data);
                Log.d("笑嘻嘻", "29");
                dos.flush();
                Log.d("笑嘻嘻", "30");
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
                Log.d("笑嘻嘻", "31");
                if (res == 200) {
                    return SUCCESS;
                }
            }
        } catch (MalformedURLException e) {
            Log.d("笑嘻嘻", "dd");
            Log.d("笑嘻嘻", "dd" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("笑嘻嘻", "gg");
            Log.d("笑嘻嘻", "gg" + e.getMessage() + "gg" + e.getLocalizedMessage() + "gg" + e.toString() + "gg" + e.getCause() + "gg" + e.getStackTrace());
            e.printStackTrace();
        }
        Log.d("笑嘻嘻", "32");
        return FAILURE;
    }
}