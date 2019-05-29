package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

import pub.devrel.easypermissions.EasyPermissions;

//import android.opengl.Matrix;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, AdapterView.OnItemSelectedListener {

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student);
//        // 首先获取到意图对象
//        Intent intent = getIntent();
//
//        // 获取到传递过来的邮箱，身份
//        String email = intent.getStringExtra("email");
////        String account = intent.getStringExtra("account");
//        Log.d("学生", "email" + email);
////        Log.d("学生","account"+account);
//    }
    private ImageView ivTest_1, ivTest_2,ivTest_3;
    private TextView dbm_view;

    private int camera_tag;
    private File cameraSavePath, cameraSavePath_1, cameraSavePath_2, cameraSavePath_3;//拍照照片路径
    private Uri uri;
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private String return_value;

    private String email;
    private CountDownLatch count, count2, count3;
    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private int course_sum;
    private String[] listTeacherName, listCourseName, listCourseId, listTeacherId;

    private Calendar cal;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String minute;
    private String second;
    private String attendance_time;

    private int dbm;
    private String courseId, teacherId;
    private String bssid;

    private WifiInfo wifiInfo = null;       //获得的Wifi信息
    private WifiManager wifiManager = null; //Wifi管理器
    private Handler handler;
    private static Handler process_handler =new Handler();
    private int level;                      //信号强度值
    private String macAddress;              //wifi信号BSSID值
    List<ScanResult> listb;
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private String[] listSSID0;
    private String[] listBSSID0;
    private int[] listLevel0;

    private AlertDialog.Builder process_builder;
    private AlertDialog process_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        // 首先获取到意图对象
        Intent intent = getIntent();

        // 获取到传递过来的邮箱，身份
        email = intent.getStringExtra("email");
//        String account = intent.getStringExtra("account");
        Log.d("学生", "email" + email);
//        Log.d("学生","account"+account);

        Button btnGetPicFromCamera_1 = findViewById(R.id.btn_get_pic_from_camera_1);
        Button btnGetPicFromCamera_2 = findViewById(R.id.btn_get_pic_from_camera_2);
        Button btnGetPicFromCamera_3 = findViewById(R.id.btn_get_pic_from_camera_3);
//        Button btnGetPicFromPhotoAlbum = findViewById(R.id.btn_get_pic_form_photo_album);
//        Button btnGetPermission = findViewById(R.id.btn_get_Permission);
        Button btn_upload_img = findViewById(R.id.btn_upload_img);
        ivTest_1 = findViewById(R.id.iv_test_1);
        ivTest_2 = findViewById(R.id.iv_test_2);
        ivTest_3 = findViewById(R.id.iv_test_3);

        dbm_view = findViewById(R.id.dbm_view);

        btnGetPicFromCamera_1.setOnClickListener(this);
        btnGetPicFromCamera_2.setOnClickListener(this);
        btnGetPicFromCamera_3.setOnClickListener(this);
//        btnGetPicFromPhotoAlbum.setOnClickListener(this);
//        btnGetPermission.setOnClickListener(this);
        btn_upload_img.setOnClickListener(this);
        getPermission();

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

        count = new CountDownLatch(1);//等待一个线程结束后才执行其他步骤
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDao ud = new UserDao();
                ResultSet result = ud.StudentCheckCourses(email);
                try {
                    result.last();
                    Log.d("学生查课程","result.getRow()"+result.getRow());
                    listCourseName = new String[result.getRow()];
                    listTeacherName = new String[result.getRow()];
                    listCourseId = new String[result.getRow()];
                    listTeacherId = new String[result.getRow()];
                    result.beforeFirst();
                    course_sum = 0;
                    while (result.next()){
                        Log.d("学生查课程", String.valueOf(result.getInt(1)));
                        String atten = ud.StudentCheckCourses_step2(result.getString(1), result.getString(2));
                        if(atten.contentEquals("1")){
                            String result2 = ud.StudentCheckCourses_course(result.getString(1));
                            String result3 = ud.StudentCheckCourses_teacher(result.getString(2));
                            Log.d("查课程咯", result2);
                            Log.d("查课程咯", result3);
                            listCourseId[course_sum] = result.getString(1);
                            listTeacherId[course_sum] = result.getString(2);
                            listCourseName[course_sum] = result2;
                            listTeacherName[course_sum] = result3;
                            course_sum++;
                        }
                    }
                }catch (SQLException e) {
                    Log.d("学生查课程","error"+e.getMessage());
                }
                count.countDown();
            }
        }).start();
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("学生查课程","线程结束了");
        spinner = findViewById(R.id.spinner);
        data_list = new ArrayList<String>();
        for(int i=0;i<course_sum;i++){
            data_list.add(listCourseId[i]+":"+listCourseName[i]+"-"+listTeacherId[i]+":"+listTeacherName[i]);
        }
        arr_adapter = new ArrayAdapter<String>(StudentActivity.this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

        spinner.setOnItemSelectedListener(this);

        process_builder = new AlertDialog.Builder(StudentActivity.this)
                .setMessage("处理中...").setCancelable(true);
        process_dialog = process_builder.create();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_get_pic_from_camera_1:
                cameraSavePath_1 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
                cameraSavePath = cameraSavePath_1;
                camera_tag = 1;
                goCamera();
                break;
            case R.id.btn_get_pic_from_camera_2:
                cameraSavePath_2 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
                cameraSavePath = cameraSavePath_2;
                camera_tag = 2;
                goCamera();
                break;
            case R.id.btn_get_pic_from_camera_3:
                cameraSavePath_3 = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
                cameraSavePath = cameraSavePath_3;
                camera_tag = 3;
                goCamera();
                break;
            case R.id.btn_upload_img:
                // Android 4.0 之后不能在主线程中请求HTTP请求



//                count3 = new CountDownLatch(1);//等待一个线程结束后才执行其他步骤
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        process_handler.post(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });

                        UserDao usd = new UserDao();
                        String id = usd.findId(email);
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
                        attendance_time = year + "-" + month + "-" + day +" " +hour + ":" + minute + ":" + second;
                        Log.d("时间","北京时间 "+attendance_time);

                        UserDao ud = new UserDao();
                        courseId = spinner.getSelectedItem().toString().split("-")[0].split(":")[0];
                        teacherId = spinner.getSelectedItem().toString().split("-")[1].split(":")[0];
                        String stu_result = ud.StudentAttendance(email, courseId, teacherId, attendance_time, String.valueOf(dbm));
                        Log.d("学生签到","stu_result"+stu_result);

                        HttpAssist post_access = new HttpAssist();
                        Log.d("笑嘻嘻", cameraSavePath_1 + ":cameraSavePath_1");
                        Log.d("笑嘻嘻", cameraSavePath_2 + ":cameraSavePath_2");
                        Log.d("笑嘻嘻", cameraSavePath_3 + ":cameraSavePath_3");
                        return_value = post_access.uploadFile(cameraSavePath_1,cameraSavePath_2,cameraSavePath_3, id, stu_result);
                        Log.d("笑嘻嘻", return_value + "哈哈");
//                        if(!TextUtils.isEmpty(stu_result)){
                        if(!TextUtils.isEmpty(stu_result) && return_value.contentEquals("1")){
                            Log.d("学生签到","提交成功，等待考核");
                            Intent intent = new Intent();
                            //setClass函数的第一个参数是一个Context对象
                            //Context是一个类，Activity是Context类的子类，也就是说，所有的Activity对象，都可以向上转型为Context对象
                            //setClass函数的第二个参数是一个Class对象，在当前场景下，应该传入需要被启动的Activity类的class对象
                            intent.setClass(StudentActivity.this, WaitForAttenActivity.class);
                            startActivity(intent);
                        }else{
                            Log.d("学生签到","提交失败");
                            Intent intent = new Intent();
                            //setClass函数的第一个参数是一个Context对象
                            //Context是一个类，Activity是Context类的子类，也就是说，所有的Activity对象，都可以向上转型为Context对象
                            //setClass函数的第二个参数是一个Class对象，在当前场景下，应该传入需要被启动的Activity类的class对象
                            intent.setClass(StudentActivity.this, TwiceForbiddenActivity.class);
                            startActivity(intent);
                        }
                        process_dialog.dismiss(); //关闭弹窗
//                        count3.countDown();
                    }
                }).start();
                process_dialog.show();
//                try {
//                    count3.await();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
                break;

//            case R.id.btn_get_pic_form_photo_album:
//                goPhotoAlbum();
//                break;
//            case R.id.btn_get_Permission:
//                getPermission();
//                break;
        }
    }

    //获取权限
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            Toast.makeText(this, "已经申请相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取您的使用权限", 1, permissions);
        }

    }

    //激活相机操作
    private void goCamera() {
//        cameraSavePath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
//        cameraSavePath = new File(Environment.getDownloadCacheDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");

        Log.d("笑嘻嘻", "嘿" + Environment.getDataDirectory().getAbsolutePath());
        Log.d("笑嘻嘻", "嘿" + Environment.getDataDirectory().getPath());
        Log.d("笑嘻嘻", "嘿" + Environment.getDataDirectory().getParent());
        Log.d("笑嘻嘻", "嘿" + Environment.getDownloadCacheDirectory().getAbsolutePath());
        Log.d("笑嘻嘻", "嘿" + Environment.getDownloadCacheDirectory().getPath());
        Log.d("笑嘻嘻", "嘿" + Environment.getDownloadCacheDirectory().getParent());
        Log.d("笑嘻嘻", "嘿" + Environment.getRootDirectory().getAbsolutePath());
        Log.d("笑嘻嘻", "嘿" + Environment.getRootDirectory().getPath());
        Log.d("笑嘻嘻", "嘿" + Environment.getRootDirectory().getParent());
        Log.d("笑嘻嘻", "嘿" + Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.d("笑嘻嘻", "嘿" + Environment.getExternalStorageDirectory().getPath());
        Log.d("笑嘻嘻", "嘿" + Environment.getExternalStorageDirectory().getParent());
//        cameraSavePath = new File(Environment.getDataDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Log.d("相机","1");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //安卓7.0
            uri = FileProvider.getUriForFile(StudentActivity.this, "com.example.myapplication.fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        Log.d("相机","2");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Log.d("相机","3");
        StudentActivity.this.startActivityForResult(intent, 1);
        Log.d("相机","4");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //框架要求必须这么写
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }

    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("相机","5");
        int angle = readPictureDegree(String.valueOf(cameraSavePath));
        Log.e("TAG","degree===="+angle);
        Bitmap bitmapori = BitmapFactory.decodeFile(String.valueOf(cameraSavePath));
        // 修复图片被旋转的角度
        Bitmap bitmap = rotaingImageView(angle, bitmapori);

        try {
//            File file = new File(dir + fileName + ".jpg");
            FileOutputStream out = new FileOutputStream(cameraSavePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String photoPath;
        switch (camera_tag){
            case 1:
                cameraSavePath_1 = cameraSavePath;
                if (requestCode == 1 && resultCode == RESULT_OK) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        photoPath = String.valueOf(cameraSavePath_1);
                    } else {
                        photoPath = uri.getEncodedPath();
                    }
                    Log.d("拍照返回图片路径", photoPath);
                    Glide.with(StudentActivity.this).load(photoPath).into(ivTest_1);
                }
                break;
            case 2:
                cameraSavePath_2 = cameraSavePath;
                if (requestCode == 1 && resultCode == RESULT_OK) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        photoPath = String.valueOf(cameraSavePath_2);
                    } else {
                        photoPath = uri.getEncodedPath();
                    }
                    Log.d("拍照返回图片路径", photoPath);
                    Glide.with(StudentActivity.this).load(photoPath).into(ivTest_2);
                }
                break;
            case 3:
                cameraSavePath_3 = cameraSavePath;
                if (requestCode == 1 && resultCode == RESULT_OK) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        photoPath = String.valueOf(cameraSavePath_3);
                    } else {
                        photoPath = uri.getEncodedPath();
                    }
                    Log.d("拍照返回图片路径", photoPath);
                    Glide.with(StudentActivity.this).load(photoPath).into(ivTest_3);
                }
                break;
        }

//        else if (requestCode == 2 && resultCode == RESULT_OK) {
//            photoPath = getPhotoFromPhotoAlbum.getRealPathFromUri(this, data.getData());
//            Glide.with(StudentActivity.this).load(photoPath).into(ivTest);
//        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int j, long l) {
        //从适配器里面获取选择的文本，当然也可以从list中获取 list.get[j]
        //以上方法中的int j，指的是选择了第几项
        courseId = spinner.getSelectedItem().toString().split("-")[0].split(":")[0];
        teacherId = spinner.getSelectedItem().toString().split("-")[1].split(":")[0];
        Log.d("更改选项","1");
        String get_dbm =  arr_adapter.getItem(j);
        Log.d("更改选项",get_dbm);
        count2 = new CountDownLatch(1);//等待一个线程结束后才执行其他步骤
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDao ud = new UserDao();
                Log.d("匹配MAC",courseId+":"+teacherId);
                bssid = ud.findMac(courseId, teacherId);
                count2.countDown();
            }
        }).start();
        try {
            count2.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("学生查课程","第二个线程结束了");

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
        dbm = -200; //不能匹配备用
        for (int i = 0; i < listb.size(); i++) {
            Log.d("匹配MAC",listBSSID0[i]);
            Log.d("匹配MAC",bssid);
            if(listBSSID0[i].contentEquals(bssid)){
                dbm = listLevel0[i];
            }
            Log.d("wifi", listSSID0[i] + "//" + listBSSID0[i] + "//" + listLevel0[i]);
        }
        Log.d("wifi", "=======================");
        dbm_view.setText(String.valueOf(dbm));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.e("TAG", "原图被旋转角度： ========== " + orientation );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Log.e("TAG","angle==="+angle);
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

}
