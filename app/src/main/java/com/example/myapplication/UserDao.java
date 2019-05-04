package com.example.myapplication;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.mysql.jdbc.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;


public class UserDao {

    JdbcUtil jdbcUtil = JdbcUtil.getInstance();
    //第一个参数为数据库名称，第二个参数为数据库账号 第三个参数为数据库密码
    Connection conn = jdbcUtil.getConnection("test2", "root", "QWqw12!@");
    //注册
    private static final String AES = "AES";//AES 加密
    String secret_key = "QWqw12!@QWqw12!@";
//    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";//AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式

    public boolean register(String name, String password) {
        if (conn == null) {
            Log.i(TAG, "register:conn is null");
            return false;
        } else {
            //进行数据库操作
            String sql = "insert into app_admininfo(email,password) values(?,?)";
            try {
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, name);
                pre.setString(2, password);
                return pre.execute();
            } catch (SQLException e) {
                return false;
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String register_attendance(String chosen_mac, String chosen_course, String start_time, String email){
        if (conn == null) {
            Log.i(TAG, "register:conn is null");
            return null;
        }
        else {
            //进行数据库操作
            String sql = "insert into app_attendanceinfo(bssid,attendance_start_time,attendance_tag,course_id_id, teacher_id_id) values(?,?,?,?,(select teacherNum from app_teacherinfo where email=?))";
            String sql_2 = "select attendance_id from app_attendanceinfo order by attendance_id desc LIMIT 1;"; //查询最后一条记录
            try {
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, chosen_mac);
                pre.setString(2, start_time);
                pre.setString(3, "1");
                pre.setString(4, chosen_course);
                pre.setString(5, email);
                pre.execute();

                pre = conn.prepareStatement(sql_2);
                ResultSet result = pre.executeQuery();
                result.next();
//                Log.d("插入考勤","Dao:"+result.getString(1));
                return result.getString(1);
            } catch (SQLException e) {
                Log.d("插入考勤",e.getMessage());
                return null;
            }
        }
    }

    public int setEndAttendanceTime(String attendance_id, String end_time){
        if (conn == null) {
            Log.i(TAG, "register:conn is null");
            return 0;
        }
        else {
            String sql = "update app_attendanceinfo set attendance_end_time=?,attendance_tag=? where attendance_id=?";
            try{
                PreparedStatement pre = conn.prepareStatement(sql);
                pre.setString(1, end_time);
                pre.setString(2, "0");
                pre.setString(3, attendance_id);
                return pre.executeUpdate();
            } catch (SQLException e) {
                Log.d("结束考勤",e.getMessage());
                return 0;
            }
        }
    }



    public static String encrypt(String key, String cleartext) {
        if (TextUtils.isEmpty(cleartext)) {
            Log.d("笑嘻嘻","1");
            return cleartext;
        }
        try {
            byte[] result = encrypt(key, cleartext.getBytes());
            Log.d("笑嘻嘻","2");
            return new String(Base64.encode(result,Base64.DEFAULT));
        } catch (Exception e) {
            Log.d("笑嘻嘻","3");
            e.printStackTrace();
        }
        Log.d("笑嘻嘻","4");
        return null;
    }

    /*
     * 加密
     */
    private static byte[] encrypt(String key, byte[] clear) throws Exception {
        byte[] raw = key.getBytes();
        Log.d("笑嘻嘻","5");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, AES);
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }



    //登录
    public boolean login(String name, String password, String account) {
        if (conn == null) {
            Log.i(TAG, "register:conn is null");
            return false;
        } else {
            String sql = "select * from app_userinfo where email=? and password=?";
            //默认为学生
            if(account.contentEquals("rd2")){ //教师
                sql = "select * from app_teacherinfo where email=? and password=?";
            }
            else if(account.contentEquals("rd3")){ //管理员
                sql = "select * from app_admininfo where email=? and password=?";
            }
            try {
                String password_copy = password;

                if(password.length()<16){
                    for(int i=0;i<(16-password.length());i++){
                        password_copy += ' ';
                    }
                }
                else{
                    password_copy = password;
                }
                Log.d("笑嘻嘻","password_length"+password_copy.length());
                Log.d("笑嘻嘻","password"+password_copy);
                String encryStr = encrypt(secret_key, password_copy);
                Log.d("笑嘻嘻",encryStr+"哈哈");
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, name);
//                pres.setString(2, encryStr);
                pres.setString(2, encryStr.replaceAll("\n",""));
                Log.d("笑嘻嘻",pres+"哈哈pres");
                ResultSet res = pres.executeQuery();
                Log.d("笑嘻嘻",res+"哈哈res");
//                Log.d("笑嘻嘻",res.next()+"哈哈res.next()");
                String sql_2 = "select * from app_admininfo where email='admin@qq.com' and password='Neiz5WXbTTFKNcMkYMgFFg=='";
                PreparedStatement pres_2 = conn.prepareStatement(sql_2);
                ResultSet res_2 = pres_2.executeQuery();
                Log.d("笑嘻嘻",res_2+"哈哈res_2");
                Log.d("笑嘻嘻",res_2.next()+"哈哈res_2.next()");
                boolean t = res.next();
                return t;
            } catch (SQLException e) {

                return false;
            }

        }
    }

    public ResultSet TeacherCheckCourses(String email){
        if (conn == null) {
            Log.d("查课程","1");
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            try {
                Log.d("查课程","2");
//                select * from app_courseinfo where courseNum = (
                String sql_course = "select course_id_id from app_teacher2course where teacher_id_id = (select teacherNum from app_teacherinfo where email =?)";
                Log.d("查课程","3");
                PreparedStatement pres = conn.prepareStatement(sql_course);
                Log.d("查课程","4");
                pres.setString(1, email);
                Log.d("查课程","5");
                ResultSet res = pres.executeQuery();
                Log.d("查课程",res.toString());
//                while (res.next()){
//                    Log.d("查课程", "6");
//                    Log.d("查课程", String.valueOf(res.getInt(1)));
//                    break;
//                }
                return res;
            }catch (SQLException e) {
                Log.d("查课程","error"+e.getMessage());
                return null;
            }

        }
    }

    public ResultSet TeacherCheckCourses_step2(String courseNum){
        if (conn == null) {
            Log.d("查课程","1");
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            try {
                Log.d("查课程","2");

                String sql_course = "select * from app_courseinfo where courseNum =?";
                Log.d("查课程","3");
                PreparedStatement pres = conn.prepareStatement(sql_course);
                Log.d("查课程","4");
                pres.setString(1, courseNum);
                Log.d("查课程","5");
                ResultSet res = pres.executeQuery();
                Log.d("查课程",res.toString());
//                while (res.next()){
//                    Log.d("查课程", "6");
//                    Log.d("查课程", String.valueOf(res.getInt(1)));
//                    break;
//                }
                return res;
            }catch (SQLException e) {
                Log.d("查课程","error"+e.getMessage());
                return null;
            }

        }
    }

    public ResultSet StudentCheckCourses(String email){
        if (conn == null) {
            Log.d("学生查课程","1");
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            String sql = "select cour_id,teac_id from app_choose_course where stu_id = (select studentNum from app_userinfo where email=?)";
            try {
                //select studentNum from app_userinfo where email=?
                //select cour_id,teac_id from app_choose_course where stu_id = (select studentNum from app_userinfo where email=?)
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, email);
                ResultSet res = pres.executeQuery();
                Log.d("学生查课程",res.toString());
                return res;
            }catch (SQLException e) {
                Log.d("学生查课程","error"+e.getMessage());
                return null;
            }
        }
    }

    public String StudentCheckCourses_step2(String course_id, String teacher_id){
        if (conn == null) {
            Log.d("学生查课程","1");
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            String sql = "select attendance_tag from app_attendanceinfo where course_id_id=? and teacher_id_id=?";
            try{
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, course_id);
                pres.setString(2, teacher_id);
                ResultSet res = pres.executeQuery();
                while(res.next()){
                    Log.d("学生查课程",res.getString(1));
                    if (res.getString(1).contentEquals("1")){
                        break;
                    }
                }
                return res.getString(1);
            }catch (SQLException e) {
                Log.d("学生查课程","error"+e.getMessage());
                return "0";
            }
        }
    }

    public String StudentCheckCourses_course(String course_id){
        if (conn == null) {
            Log.d("学生查课程","1");
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            try{
                String sql = "select courseName from app_courseinfo where courseNum=?";
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1, course_id);
                ResultSet res = pres.executeQuery();
                res.next();
                Log.d("学生查课程",res.getString(1));
                return res.getString(1);
            }catch (SQLException e) {
                Log.d("学生查课程","error"+e.getMessage());
                return null;
            }
        }
    }

    public String StudentCheckCourses_teacher(String teacher_id){
            if (conn == null) {
                Log.d("学生查课程","1");
                Log.i(TAG, "register:conn is null");
                return null;
            } else {
                try{
                    String sql = "select teacherName from app_teacherinfo where teacherNum=?";
                    PreparedStatement pres = conn.prepareStatement(sql);
                    pres.setString(1, teacher_id);
                    ResultSet res = pres.executeQuery();
                    res.next();
                    Log.d("学生查课程",res.getString(1));
                    return res.getString(1);
                }catch (SQLException e) {
                    Log.d("学生查课程","error"+e.getMessage());
                    return null;
                }
            }
    }

    public String StudentAttendance(String email, String courseId, String teacherId, String attendance_time, String dbm){
        if (conn == null) {
            Log.d("学生签到","1");
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            try{
                //select attendance_id from app_attendanceinfo where course_id_id=? and teacher_id_id=? and attendance_tag='1'
                String sql ="insert into app_attendance(tag,attendance_time,stu_id,att_id, dbm) values(?,?,(select studentNum from app_userinfo where email=?),(select attendance_id from app_attendanceinfo where course_id_id=? and teacher_id_id=? and attendance_tag=?),?)";
                PreparedStatement pres = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pres.setString(1, "2");
                pres.setString(2,attendance_time);
                pres.setString(3,email);
                pres.setString(4,courseId);
                pres.setString(5,teacherId);
                pres.setString(6,"1");
                pres.setString(7,dbm);
                pres.executeUpdate();
                ResultSet res = pres.getGeneratedKeys();
                res.last();
                return res.getString(1);
            }catch (SQLException e) {
                Log.d("学生签到","error"+e.getMessage());
                return null;
            }
        }
    }

    public String findMac(String courseId, String teacherId){
        if (conn == null) {
            Log.d("学生签到","1");
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            try {
                String sql = "select bssid from app_attendanceinfo where course_id_id=? and teacher_id_id=? and attendance_tag=?";
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1,courseId);
                pres.setString(2,teacherId);
                pres.setString(3,"1");
                ResultSet res = pres.executeQuery();
                res.next();
                Log.d("匹配MAC","findMac():"+res.getString(1));
                return res.getString(1);
            }catch (SQLException e) {
                Log.d("学生签到","error"+e.getMessage());
                return null;
            }
        }
    }

    public String findId(String email) {
        if (conn == null) {
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            String sql = "select * from app_userinfo where email=?";
            try {
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1,email);
                ResultSet res = pres.executeQuery();
                res.next();
                return res.getString(1);
            } catch (SQLException e) {
                return null;
            }
        }
    }

    public String findAttendanceId(String courseId, String teacherId) {
        if (conn == null) {
            Log.i(TAG, "register:conn is null");
            return null;
        } else {
            try {
                String sql = "select bssid from app_attendanceinfo where course_id_id=? and teacher_id_id=? and attendance_tag=?";
                PreparedStatement pres = conn.prepareStatement(sql);
                pres.setString(1,courseId);
                pres.setString(2,teacherId);
                pres.setString(3,"1");
                ResultSet res = pres.executeQuery();
                res.next();
                return res.getString(1);
            }catch (SQLException e) {
                return null;
            }
        }
    }

}

