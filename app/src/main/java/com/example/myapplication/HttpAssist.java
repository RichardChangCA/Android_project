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

    public static String uploadFile(File file, File file2, File file3, String id, String attenId) {
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        String PREFIX = "--", LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; // 内容类型
        String RequestURL = "http://39.97.105.151:8000/stu_upload/";
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); // 允许输入流
            conn.setDoOutput(true); // 允许输出流
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestMethod("POST"); // 请求方式
            conn.setRequestProperty("Charset", CHARSET); // 设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
                    + BOUNDARY);
            if (file != null) {
                /**
                 * 当文件不为空，把文件包装并且上传
                 */
                //conn.setDoOutput(true);
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                StringBuffer sb2 = new StringBuffer();
                StringBuffer sb3 = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */

                sb.append("Content-Disposition: form-data; name=\"id\"" + LINE_END);
                sb.append(LINE_END);
                sb.append(id + LINE_END);
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"atten_id\"" + LINE_END);
                sb.append(LINE_END);
                sb.append(attenId + LINE_END);
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                sb.append("Content-Disposition: form-data; name=\"img1\"; filename=\""
                        + file.getName() + "\"" + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());



                sb2.append(PREFIX);
                sb2.append(BOUNDARY);
                sb2.append(LINE_END);
                sb2.append("Content-Disposition: form-data; name=\"img2\"; filename=\""
                        + file2.getName() + "\"" + LINE_END);
                sb2.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb2.append(LINE_END);
                dos.write(sb2.toString().getBytes());
                is = new FileInputStream(file2);
                bytes = new byte[1024];
                len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());

                sb3.append(PREFIX);
                sb3.append(BOUNDARY);
                sb3.append(LINE_END);
                sb3.append("Content-Disposition: form-data; name=\"img3\"; filename=\""
                        + file3.getName() + "\"" + LINE_END);
                sb3.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINE_END);
                sb3.append(LINE_END);
                dos.write(sb3.toString().getBytes());
                is = new FileInputStream(file3);
                bytes = new byte[1024];
                len = 0;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());




                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
                        .getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功 当响应成功，获取响应的流
                 */
                int res = conn.getResponseCode();
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