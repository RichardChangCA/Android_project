package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.Calendar;
import java.util.TimeZone;

public class StopAttendanceActivity extends AppCompatActivity {

    String attendance_id;

    private Calendar cal;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private String second;
    private String end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_attendance);

        Intent intent = getIntent();

        // 获取到传递过来
        attendance_id = intent.getStringExtra("attendance_id");
    }

    public void end_process(View v) {
        switch (v.getId()) {
            case R.id.end:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
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
                        end_time = year + "-" + month + "-" + day +" " +hour + ":" + minute + ":" + second;
                        Log.d("时间","北京时间 "+end_time);
                        UserDao ud = new UserDao();
                        int return_result = ud.setEndAttendanceTime(attendance_id, end_time);
                        Log.d("结束考勤","返回结果:"+return_result);
                        if(return_result==1){
                            Log.d("结束考勤","结束成功");
                        }else{
                            Log.d("结束考勤","结束失败");
                        }

                    }
                }).start();
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.d("返回","返回键按下");
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
            Intent intent = new Intent();
            intent.setClass(StopAttendanceActivity.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
