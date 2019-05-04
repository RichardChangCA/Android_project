package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class TeacherActivity extends AppCompatActivity {

    private WifiInfo wifiInfo = null;       //获得的Wifi信息
    private WifiManager wifiManager = null; //Wifi管理器
    private Handler handler;
    private int level;                      //信号强度值
    private String macAddress;              //wifi信号BSSID值
    List<ScanResult> listb;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

    private Spinner spinner, course_spinner;
    private List<String> data_list, course_data_list;
    private ArrayAdapter<String> arr_adapter, course_arr_adapter;

    private String[] listSSID0;
    private String[] listBSSID0;
    private int[] listLevel0;
    private String[] listCourseId, listCourseName;

    private String email;

    private int course_sum;

    private CountDownLatch count;

    private String chosen_mac;
    private String chosen_course;

    private Calendar cal;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private String second;
    private String start_time;

//    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        // 首先获取到意图对象
        Intent intent = getIntent();

        // 获取到传递过来的邮箱，身份
        email = intent.getStringExtra("email");
//        String account = intent.getStringExtra("account");
        Log.d("教师", "email" + email);
//        Log.d("教师","account"+account);
        // 获得WifiManager
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        // 判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifiManager.setWifiEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //showToast("自Android 6.0开始需要打开位置权限");
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }

        // 使用定时器,每隔5秒获得一次信号强度值
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
                listb = wifiManager.getScanResults();
                Log.d("wifi", "listb" + listb);
                Log.d("wifi", "listb.size()" + listb.size());
                //数组初始化要注意
                String[] listSSID = new String[listb.size()];
                String[] listBSSID = new String[listb.size()];
                int[] listLevel = new int[listb.size()];
                if (listb != null) {
                    for (int i = 0; i < listb.size(); i++) {
                        ScanResult scanResult = listb.get(i);
                        listSSID[i] = scanResult.SSID;
                        listBSSID[i] = scanResult.BSSID;
                        listLevel[i] = scanResult.level;
                    }
                }
                listSSID0 = new String[listb.size()];
                listBSSID0 = new String[listb.size()];
                listLevel0 = new int[listb.size()];
                if (listb == null) {
                    listSSID0[0] = "NoWiFi";
                    listBSSID0[0] = "NoWiFi";
                    listLevel0[0] = -200; //-200默认没有wifi
                } else {
                    listSSID0 = listSSID;
                    listBSSID0 = listBSSID;
                    listLevel0 = listLevel;
                }
                for (int i = 0; i < listb.size(); i++) {

                    Log.d("wifi", listSSID0[i] + "//" + listBSSID0[i] + "//" + listLevel0[i]);
                }
                Log.d("wifi", "=======================");
                wifiInfo = wifiManager.getConnectionInfo();
                //获得信号强度值
                level = wifiInfo.getRssi();
                macAddress = wifiInfo.getBSSID();
//                Message msg = new Message();
//                handler.sendMessage(msg);
//            }

//        }, 1000, 5000);
//        }, 1000, 1000*60*60); //1小时
        // 使用Handler实现UI线程与Timer线程之间的信息传递,每5秒告诉UI线程获得wifiInto
//        handler = new Handler() {
//
//            @Override
//            public void handleMessage(Message msg) {

//                Toast.makeText(TeacherActivity.this,
//                        "信号强度：" + level + "  BSSID: " + macAddress, Toast.LENGTH_SHORT)
//                        .show();
                spinner = findViewById(R.id.spinner);

                //数据
                data_list = new ArrayList<String>();
                for (int i = 0; i < listb.size(); i++) {
//                    data_list.add("SSID:" + listSSID0[i]+"\r\n"+"BSSID:" + listBSSID0[i]+"\r\n"+"Level:" + listLevel0[i]);
                    if(listBSSID0[i].contentEquals(macAddress)){
                        data_list.add("优||"+listSSID0[i]+"||" + listBSSID0[i]+"||" + listLevel0[i]);
                    }else{
                        data_list.add(listSSID0[i] + "||" + listBSSID0[i] + "||" + listLevel0[i]);
                    }

                }

                //适配器
                arr_adapter = new ArrayAdapter<String>(TeacherActivity.this, android.R.layout.simple_spinner_item, data_list);
                //设置样式
                arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //加载适配器
                spinner.setAdapter(arr_adapter);
//            }
//
//        };


        count = new CountDownLatch(1);//等待一个线程结束后才执行其他步骤
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDao ud = new UserDao();
                ResultSet result = ud.TeacherCheckCourses(email);


                try {
                    result.last();
                    Log.d("查课程","result.getRow()"+result.getRow());
                    listCourseId = new String[result.getRow()];
                    listCourseName = new String[result.getRow()];
                    result.beforeFirst();
                    course_sum = 0;
                    while (result.next()){
                        Log.d("查课程", String.valueOf(result.getInt(1)));
                        ResultSet result2 = ud.TeacherCheckCourses_step2(result.getString(1));
                        while(result2.next()){
                            Log.d("查课程咯", result2.getString(1));
                            Log.d("查课程咯", result2.getString(2));
                            listCourseId[course_sum] = result2.getString(1);
                            listCourseName[course_sum] = result2.getString(2);
                            course_sum++;
//                            course_data_list.add(result2.getString(1)+":"+result2.getString(2));
                        }
                    }
                }catch (SQLException e) {
                    Log.d("查课程","error"+e.getMessage());
                }
                count.countDown();
            }
        }).start();
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("查课程","线程结束了");
        //适配器
        course_spinner = findViewById(R.id.course_spinner);
        course_data_list = new ArrayList<String>();
        for(int i=0;i<course_sum;i++){
            course_data_list.add(listCourseId[i]+":"+listCourseName[i]);
        }
        course_arr_adapter = new ArrayAdapter<String>(TeacherActivity.this, android.R.layout.simple_spinner_item, course_data_list);
        //设置样式
        course_arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        course_spinner.setAdapter(course_arr_adapter);

        cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); //中国时区

        year = String.valueOf(cal.get(Calendar.YEAR));
        if(cal.get(Calendar.MONTH)+1<10){
            month = "0"+String.valueOf(cal.get(Calendar.MONTH)+1);
        }else{
            month = String.valueOf(cal.get(Calendar.MONTH)+1);
        }
        if(cal.get(Calendar.DATE)<10){
            day = "0"+String.valueOf(cal.get(Calendar.DATE));
        }else{
            day = String.valueOf(cal.get(Calendar.DATE));
        }

        if (cal.get(Calendar.AM_PM) == 0) {
            if (cal.get(Calendar.HOUR) < 10) {
                hour = "0" + String.valueOf(cal.get(Calendar.HOUR));
            } else {
                hour = String.valueOf(cal.get(Calendar.HOUR));
            }
        }
        else {
            hour = String.valueOf(cal.get(Calendar.HOUR) + 12);
        }
        if(cal.get(Calendar.MINUTE)<10){
            minute = "0"+String.valueOf(cal.get(Calendar.MINUTE));
        }
        else{
            minute = String.valueOf(cal.get(Calendar.MINUTE));
        }
        if(cal.get(Calendar.SECOND)<10){
            second = "0"+String.valueOf(cal.get(Calendar.SECOND));
        }else{
            second = String.valueOf(cal.get(Calendar.SECOND));
        }
        start_time = year + "-" + month + "-" + day +" " +hour + ":" + minute + ":" + second;
        Log.d("时间","北京时间 "+start_time);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //permission was granted, yay! Do the contacts-related task you need to do.
                //这里进行授权被允许的处理
            } else {
                //permission denied, boo! Disable the functionality that depends on this permission.
                //这里进行权限被拒绝的处理
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void process(View v) {
        switch (v.getId()) {
            case R.id.start:
                String test = "优|张凌峰的 iPhone|a6:41:67:6c:22:67|-37";
                Log.d("被选","test"+test.split("\\|")[0]);
                Log.d("被选","test_size"+spinner.getSelectedItem().toString().split("\\|\\|").length);
                String[] temp_array = spinner.getSelectedItem().toString().split("\\|\\|");
                chosen_mac = temp_array[temp_array.length-2];
                chosen_course = course_spinner.getSelectedItem().toString().split(":")[0];
                Log.d("被选",spinner.getSelectedItem().toString());
                Log.d("被选",course_spinner.getSelectedItem().toString());
                Log.d("被选",chosen_mac);
                Log.d("被选",chosen_course);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserDao ud = new UserDao();
                        String attendance_id = ud.register_attendance(chosen_mac, chosen_course, start_time, email);
                        Log.d("插入考勤",attendance_id);

                        Intent intent = new Intent();
                        //setClass函数的第一个参数是一个Context对象
                        //Context是一个类，Activity是Context类的子类，也就是说，所有的Activity对象，都可以向上转型为Context对象
                        //setClass函数的第二个参数是一个Class对象，在当前场景下，应该传入需要被启动的Activity类的class对象
                        intent.setClass(TeacherActivity.this, StopAttendanceActivity.class);
                        intent.putExtra("attendance_id", attendance_id);
                        startActivity(intent);
                    }
                }).start();
                break;
        }
    }

}
