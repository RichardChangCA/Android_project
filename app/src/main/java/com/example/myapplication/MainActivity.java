package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

//import android.widget.TextView;
//import com.google.gson.JsonObject;

//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ////    重写
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        设置内容展示的视图
//        setContentView(R.layout.activity_main);
//        Log.d("MainActivity", "onCreate execute");
//    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        //WebView
//        WebView browser = (WebView) findViewById(R.id.Toweb);
//        browser.loadUrl("http://39.97.105.151:8000/");
//
//        //设置可自由缩放网页
//        browser.getSettings().setSupportZoom(true);
//        browser.getSettings().setBuiltInZoomControls(true);
//
//        //设置javascript动态交互权限
//        browser.getSettings().setJavaScriptEnabled(true);
//
//        // 如果页面中链接，如果希望点击链接继续在当前browser中响应，
//        // 而不是新开Android的系统browser中响应该链接，必须覆盖webview的WebViewClient对象
//        browser.setWebViewClient(new WebViewClient() {
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
//                view.loadUrl(url);
//                return true;
//            }
//        });
//    }
//
//    //go back
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        WebView browser = (WebView) findViewById(R.id.Toweb);
//        // Check if the key event was the Back button and if there's history
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && browser.canGoBack()) {
//            browser.goBack();
//            return true;
//        }
//        //  return true;
//        // If it wasn't the Back key or there's no web page history, bubble up to the default
//        // system behavior (probably exit the activity)
//        return super.onKeyDown(keyCode, event);
//    }

    private static final String TAG = "MainActivity";
    private EditText name;
    private EditText password;
    private RadioGroup account_radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        account_radio = findViewById(R.id.account_radio);
    }

    //用户根据点击事件来找到相应的功能
    public void fun(View v) {
        switch (v.getId()) {
//            case R.id.register:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String n = name.getText().toString().trim();
//                        String psw = password.getText().toString().trim();
//                        UserDao ud = new UserDao();
//                        boolean result = ud.register(n, psw);
//                        if (!result) {
//                            Looper.prepare();
//                            Toast toast = Toast.makeText(MainActivity.this, "注册成功！", Toast.LENGTH_SHORT);
//                            toast.show();
//                            Looper.loop();
//                        }
//                        Log.i(TAG, "fun" + result);
//
//                        //以上为jdbc注册
//                    }
//                }).start();
//                break;
            case R.id.login:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String n = name.getText().toString().trim();
                        String psw = password.getText().toString().trim();
                        int account = account_radio.getId();
                        int account2 = account_radio.getCheckedRadioButtonId();
                        Log.d("笑哈哈", account + "and" + account2);
                        RadioButton check_radio = findViewById(account_radio.getCheckedRadioButtonId());
                        String selectText = check_radio.getText().toString();
                        String selectText2 = check_radio.getTag().toString();
                        Log.d("笑哈哈", selectText + "and" + selectText2);
                        if (n.equals("") || psw.equals("")) {
                            Looper.prepare();
                            Toast toast = Toast.makeText(MainActivity.this, "输入不能为空！", Toast.LENGTH_SHORT);
                            toast.show();
                            Looper.loop();
                        }
                        UserDao ud = new UserDao();
                        Boolean result = ud.login(n, psw, selectText2);
                        if (!result) {
                            Looper.prepare();
                            Toast toast = Toast.makeText(MainActivity.this, "用户名不存在或密码错误！", Toast.LENGTH_SHORT);
                            toast.show();
                            Looper.loop();
                        } else {
                            Looper.prepare();
                            Toast toast = Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT);
                            toast.show();

                            //一下代码为跳转界面
                            // Intent intent=new Intent(MainActivity.this,info.class);
                            //intent.putExtra("name",n);
                            // startActivity(intent);
                            Log.d("笑哈哈","嘿"+selectText2);
                            if(selectText2.contentEquals("rd1")){
                                Log.d("笑哈哈","666");
                                Intent intent = new Intent();
                                //setClass函数的第一个参数是一个Context对象
                                //Context是一个类，Activity是Context类的子类，也就是说，所有的Activity对象，都可以向上转型为Context对象
                                //setClass函数的第二个参数是一个Class对象，在当前场景下，应该传入需要被启动的Activity类的class对象
                                intent.setClass(MainActivity.this, StudentActivity.class);
//                                intent.putExtra("account",selectText2);
                                intent.putExtra("email", n);
                                startActivity(intent);
                            }else if(selectText2.contentEquals("rd2")){
                                Log.d("笑哈哈","666");
                                Intent intent = new Intent();
                                //setClass函数的第一个参数是一个Context对象
                                //Context是一个类，Activity是Context类的子类，也就是说，所有的Activity对象，都可以向上转型为Context对象
                                //setClass函数的第二个参数是一个Class对象，在当前场景下，应该传入需要被启动的Activity类的class对象
                                intent.setClass(MainActivity.this, TeacherActivity.class);
//                                intent.putExtra("account",selectText2);
                                intent.putExtra("email", n);
                                startActivity(intent);
                            }
                            Looper.loop();

                        }

                        //以上为jdbc登录
                    }
                }).start();
                break;
            case R.id.web:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //用浏览器打开网页，有默认浏览器用默认浏览器打开，没有默认浏览器用户选择浏览器打开
                        Uri uri = Uri.parse("http://39.100.45.7:8000/");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                }).start();

        }

    }
}