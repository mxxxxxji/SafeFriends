package inno.i.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import inno.i.GlobalApplication;
import inno.i.R;

public class LoadingActivity extends AppCompatActivity {

    static String id;
    static String school_name;
    static String class_name;

    // 사용자 정보를 담을 배열
    ArrayList<String> User = new ArrayList<>();

    TextView txthello;
    ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // ui 매칭
        txthello = findViewById(R.id.LoadingActivity_textView_hello);
        progressBar = findViewById(R.id.LoadingActivity_progressBar);

        //  스레드를 위한 객체 선언
        LoadingActivity.DestinationDB ddb = new DestinationDB();
        LoadingActivity.CourseDB cdb = new CourseDB();
        LoadingActivity.ModuleDB mdb = new ModuleDB();
        LoadingActivity.VehicleDB vdb = new VehicleDB();
        LoadingActivity.TeacherDB tdb = new TeacherDB();
        LoadingActivity.StudentDB stdb = new StudentDB();
        //LoadingActivity.ChildTimeDB ctdb = new ChildTimeDB();

        // 전체화면
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 프로그레스바 생성
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(255,255,255), PorterDuff.Mode.MULTIPLY);
        }

        // 로그인 시 보낸 정보를 받는다.
        Intent intent = getIntent();
        id = intent.getStringExtra("id");


        // 유저 정보를 가져오는 메인 스레드
        Thread mThread = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {

                /* 인풋 파라메터값 생성 */
                try {
                    String param = "u_id=" + id;

                    /* 서버연결 */
                    URL url = new URL(
                            "https://innoi.co.kr/safe-school-og/test/app/call_teacher.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.connect();

                    /* 안드로이드 -> 서버 파라메터값 전달 */
                    OutputStream outs = conn.getOutputStream();
                    outs.write(param.getBytes(StandardCharsets.UTF_8));
                    outs.flush();
                    outs.close();

                    /* 서버 -> 안드로이드 파라메터값 전달 */
                    InputStream is = null;
                    BufferedReader in = null;

                    is = conn.getInputStream();
                    in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                    String line = null;
                    StringBuffer buff = new StringBuffer();

                    while ((line = in.readLine()) != null) {
                        buff.append(line + "\n");
                    }
                    String user_data = "";
                    user_data = buff.toString().trim();
                    is.close();

                    Log.e("RECV_USER_DATA", user_data);

                    // 전역 변수로 보냄
                    GlobalApplication.setUser(user_data);

                    User.addAll(Arrays.asList(user_data.split(",")));
                    GlobalApplication.setSchool_name(User.get(5));

                    Log.e("SCHOOL_NAME_DATA", GlobalApplication.getSchool_name());
                    school_name = User.get(5);
                    class_name = User.get(6);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
        try {
            mThread.join();
            // 값을 받은 후 UI 출력
            txthello.setText("\""+User.get(1) + "\""+ " 님 안녕하세요");

            // php 를 통한 데이터베이스 통신
            ddb.execute();
            cdb.execute();
            mdb.execute();
            vdb.execute();
            tdb.execute();
            stdb.execute();
            //ctdb.execute();

        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }

        // 1.5초뒤 자동으로 화면 전환
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            //Do Something
            @Override
            public void run() {
                // TODO Auto-generated get_destination stub
                Intent i = new Intent(LoadingActivity.this, SelectActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // 페이드인,아웃 효과 설정
            }
        }, 1500);

    } // onCreate

    //-----------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class DestinationDB extends AsyncTask<Void, Integer, Void> {

        String destination_data = "";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            try {
                String param = "u_school_name=" + school_name;

                /* 서버연결 */
                URL url = new URL(
                        "https://innoi.co.kr/safe-school-og/test/app/destination.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes(StandardCharsets.UTF_8));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                destination_data = buff.toString().trim();
                is.close();
                Log.e("RECV_DESTINATION_DATA", destination_data);


                GlobalApplication myApp = (GlobalApplication) getApplication();
                GlobalApplication.setDestination(destination_data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }// doinbackground
    }// DestionaionDB

    //-----------------------------------------------------------------------------------------------
    public class CourseDB extends AsyncTask<Void, Integer, Void> {

        String course_data = "";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            try {
                String param = "u_school_name=" + school_name;

                /* 서버연결 */
                URL url = new URL(
                        "https://innoi.co.kr/safe-school-og/test/app/course.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes(StandardCharsets.UTF_8));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                course_data = buff.toString().trim();
                is.close();
                Log.e("RECV_COURSE_DATA", course_data);

                GlobalApplication myApp = (GlobalApplication) getApplication();
                GlobalApplication.setCourse(course_data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }// doinbackground
    }// CourseDB


    //-----------------------------------------------------------------------------------------------
    public class StudentDB extends AsyncTask<Void, Integer, Void> {

        String student_data = "";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            try {
                String param = "u_school_name=" + school_name;

                /* 서버연결 */
                URL url = new URL(
                        "https://innoi.co.kr/safe-school-og/test/app/student.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes(StandardCharsets.UTF_8));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                student_data = buff.toString().trim();
                is.close();
                Log.e("RECV_STUDENT_DATA", student_data);

                GlobalApplication myApp = (GlobalApplication) getApplication();
                GlobalApplication.setStudent(student_data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }// doinbackground
    }// StudentDB

    //-----------------------------------------------------------------------------------------------
    public class ModuleDB extends AsyncTask<Void, Integer, Void> {

        String module_data = "";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            try {
                //반이랑 차량도 넣어야함
                String param = "u_school_name=" + school_name ;

                /* 서버연결 */
                URL url = new URL(
                        "https://innoi.co.kr/safe-school-og/test/app/module.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes(StandardCharsets.UTF_8));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }

                module_data = buff.toString().trim();
                is.close();
                Log.e("RECV_MODULE_DATA", module_data);

                GlobalApplication myApp = (GlobalApplication) getApplication();
                GlobalApplication.setModule(module_data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }// doinbackground

    }// moduleDB

    //-----------------------------------------------------------------------------------------------

    public class VehicleDB extends AsyncTask<Void, Integer, Void> {

        String vehicle_data = "";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            try {
                String param = "u_school_name=" + school_name;
                Log.e("doInBackground: ", param);
                /* 서버연결 */
                URL url = new URL(
                        "https://innoi.co.kr/safe-school-og/test/app/vehicle.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes(StandardCharsets.UTF_8));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                vehicle_data = buff.toString().trim();
                is.close();
                Log.e("RECV_VEHICLE_DATA", vehicle_data);
                GlobalApplication.setVehicle(vehicle_data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }// doinbackground
    }// vehicleDB



    // ----------------------------------------------------------------------------------------------
    public class TeacherDB extends AsyncTask<Void, Integer, Void> {

        
        String teacher_data = "";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            try {
                String param = "u_school_name=" + school_name;
                /* 서버연결 */
                URL url = new URL(
                        "https://innoi.co.kr/safe-school-og/test/app/teacher.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

                /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes(StandardCharsets.UTF_8));
                outs.flush();
                outs.close();

                /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);

                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {
                    buff.append(line + "\n");
                }
                teacher_data = buff.toString().trim();
                is.close();
                Log.e("RECV_TEACHER_DATA", teacher_data);
                GlobalApplication.setTeacher(teacher_data);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }// doinbackground
    }// teacherDB
}
