package inno.i.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MulticastSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import inno.i.R;


public class LoginActivity extends AppCompatActivity {

    EditText et_id, et_pwd; // 아이디,비밀번호 editbox
    String id, pwd; // 정보를 저장할 문자열 정의
    final Context context = this; // 로그인 결과 확인용
    Button button_login;//로그인 버튼
    CheckBox chk_save;//로그인 상태 유지 체크박스

    //SharedPreferences : 데이터를 저장할 수 있는 클래스
    //자동로그인 및 ID/PW저장을 위해 쓰임
    SharedPreferences login_information;
    SharedPreferences.Editor editor;

    //id/pwd 이미지 뷰
    ImageView id_iv;
    ImageView pwd_iv;
    //------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 전체화면
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // ui 매칭
        button_login = findViewById(R.id.LoginActivity_button_login);
        et_id = findViewById(R.id.LoginActivity_editText_id);
        et_pwd = findViewById(R.id.LoginActivity_editText_pw);
        chk_save = findViewById(R.id.save_login_checkbox);
        id_iv = findViewById(R.id.id_iv);
        pwd_iv = findViewById(R.id.pwd_iv);



        //"setting"은 파일이름이라 생각하면됨.
        login_information = getSharedPreferences("setting", 0);
        //setting에 id와 pw저장
        editor = login_information.edit();

        //자동로그인
     /*   if(login_information.getBoolean("chk_auto", false))
        {
            Log.e("auto","auto");
            id = login_information.getString("ID", "");
            pwd = login_information.getString("PW", "");

            et_id.setText(login_information.getString("ID", ""));
            et_pwd.setText(login_information.getString("PW", ""));
            // php 를 통한 로그인
            loginDB lDB = new loginDB();
            lDB.execute();
            chk_auto.setChecked(true);
        }*/
        //ID/PW저장
        if(login_information.getBoolean("chk_save", false)){
            Log.e("save","save");
            et_id.setText(login_information.getString("ID", ""));
            et_pwd.setText(login_information.getString("PW", ""));
            chk_save.setChecked(true);
        }

        //edit박스 입력 시 버튼색깔 변경
        et_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                button_login.setBackgroundResource(R.drawable.login_btn2);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                button_login.setBackgroundResource(R.drawable.login_btn2);
            }

            @Override
            public void afterTextChanged(Editable s) {
                id_iv.setImageResource(R.drawable.icon_identity_y);

            }


        });
        et_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                button_login.setBackgroundResource(R.drawable.login_btn);
                pwd_iv.setImageResource(R.drawable.icon_lock_password_y);
            }
        });

        //자동로그인 체크 시
        /*chk_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String ID = et_id.getText().toString();
                    String PW = et_pwd.getText().toString();

                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("chk_auto", true);
                    editor.commit();//저장
                }else{
                    editor.clear();
                    editor.commit();
                }
            }
        });
*/
        //ID/PW저장 체크시
       chk_save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String ID = et_id.getText().toString();
                    String PW = et_pwd.getText().toString();

                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("chk_save", true);
                    editor.commit();//저장
                }else{
                    editor.clear();
                    editor.commit();
                }
            }
        });

        //로그인 버튼 클릭 시
        button_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    //
                    id = et_id.getText().toString();
                    pwd = et_pwd.getText().toString();


                    //인터넷이 연결되지 않았을경우
                    if(Get_Internet()==0){
                        // 다이얼로그 빌더 선언
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = LayoutInflater.from(context);

                        // 다이얼로그 레이아웃 ui 선언
                        View view = inflater.inflate(R.layout.popup_confirm, null);

                        // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                        Button button = view.findViewById(R.id.btn_confirm);
                        TextView content_tv = view.findViewById(R.id.tv_content);

                        // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                        builder.setCancelable(false);

                        // 빌더 레이아웃 연결
                        builder.setView(view);

                        // 다이얼로그 생성
                        final AlertDialog dig = builder.create();

                        // 다이얼로그 출력
                        dig.show();

                        // 다이얼로그 크기를 위해 현재 화면 비율을 가져온다.
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);

                        // 비율에 맞게 다이얼로그 크기를 지정
                        Window window = dig.getWindow();

                        // 가로 화면의 80% 세로 30%
                        int x = (int) (size.x * 0.8f);
                        int y = (int) (size.y * 0.3f);

                        // 다이얼로그 크기 조정
                        window.setLayout(x, y);


                        // 다이얼로그 텍스트 변경
                        content_tv.setText("인터넷이 연결되지 않았습니다.");

                        // 확인 버튼 클릭 리스너
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });
                        return;
                    }else{
                     /*   if(chk_auto.isChecked()){
                            String ID = et_id.getText().toString();
                            String PW = et_pwd.getText().toString();

                            editor.putString("ID", ID);
                            editor.putString("PW", PW);
                            editor.putBoolean("chk_auto", true);
                            editor.commit();//저장
                        }else */if(chk_save.isChecked()){
                            String ID = et_id.getText().toString();
                            String PW = et_pwd.getText().toString();

                            editor.putString("ID", ID);
                            editor.putString("PW", PW);
                            editor.putBoolean("chk_save", true);
                            editor.commit();//저장
                        }else{
                            editor.clear();
                            editor.commit();
                        }
                        // 아이디가 비어있다면
                        if (id.getBytes().length <= 0) {
                            // 다이얼로그 빌더 선언
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = LayoutInflater.from(context);

                            // 다이얼로그 레이아웃 ui 선언
                            View view = inflater.inflate(R.layout.popup_confirm, null);

                            // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                            Button button = view.findViewById(R.id.btn_confirm);

                            TextView content_tv = view.findViewById(R.id.tv_content);

                            // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                            builder.setCancelable(false);

                            // 빌더 레이아웃 연결
                            builder.setView(view);

                            // 다이얼로그 생성
                            final AlertDialog dig = builder.create();

                            // 다이얼로그 출력
                            dig.show();

                            // 다이얼로그 크기를 위해 현재 화면 비율을 가져온다.
                            Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);

                            // 비율에 맞게 다이얼로그 크기를 지정
                            Window window = dig.getWindow();

                            // 가로 화면의 80% 세로 30%
                            int x = (int) (size.x * 0.8f);
                            int y = (int) (size.y * 0.3f);


                            // 다이얼로그 크기 조정
                            window.setLayout(x, y);


                            // 다이얼로그 텍스트 변경
                            content_tv.setText("아이디를 입력해주세요.");

                            // 확인 버튼 클릭 리스너
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 다이얼로그 제거
                                    dig.dismiss();
                                }
                            });
                            return;
                        }
                    }

                } catch (NullPointerException e) {
                    Log.e("err", e.getMessage());
                }

                // php 를 통한 로그인
                loginDB lDB = new loginDB();
                lDB.execute();


            }
        });


    }

    //------------------------------------------------------------------------------------------------------------------------------------
    public class loginDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + id + "&u_pw=" + pwd + "";

            try {
                Log.e("check", "start");

                /* 서버연결 */
                URL url = new URL("https://innoi.co.kr/safe-school-og/test/app/login_teacher.php");
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
                    buff.append(line);
                }

                data = buff.toString().trim();


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // 스레드 동작 후
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            data = StringReplace(data);
//            Log.e("RECV", "<<"+data+">>");
//            Log.e("RECV_LENGHT", ""+data.length());
            // 1이면 로그인 성공
            if (data.equals("1"))
            {
                Intent intent = new Intent(LoginActivity.this, LoadingActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            // 0이면 비밀번호 불 일치
            else if (data.equals("0")) {

                // 다이얼로그 빌더 선언
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);

                // 다이얼로그 레이아웃 ui 선언
                View view = inflater.inflate(R.layout.popup_confirm, null);

                // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                Button button = view.findViewById(R.id.btn_confirm);
                TextView content_tv = view.findViewById(R.id.tv_content);

                // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                builder.setCancelable(false);

                // 빌더 레이아웃 연결
                builder.setView(view);

                // 다이얼로그 생성
                final AlertDialog dig = builder.create();

                // 다이얼로그 출력
                dig.show();

                // 다이얼로그 크기를 위해 현재 화면 비율을 가져온다.
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                // 비율에 맞게 다이얼로그 크기를 지정
                Window window = dig.getWindow();

                // 가로 화면의 80% 세로 30%
                int x = (int) (size.x * 0.8f);
                int y = (int) (size.y * 0.3f);

                // 다이얼로그 크기 조정
                window.setLayout(x, y);

                // 다이얼로그 텍스트 변경
                content_tv.setText("비밀번호가 일치하지 않습니다.");

                // 확인 버튼 클릭 리스너
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 다이얼로그 제거
                        dig.dismiss();
                    }
                });


                // 그 외 없는 아이디
            } else {
                // 다이얼로그 빌더 선언
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);

                // 다이얼로그 레이아웃 ui 선언
                View view = inflater.inflate(R.layout.popup_confirm, null);

                // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                Button button = view.findViewById(R.id.btn_confirm);

                TextView content_tv = view.findViewById(R.id.tv_content);

                // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                builder.setCancelable(false);

                // 빌더 레이아웃 연결
                builder.setView(view);

                // 다이얼로그 생성
                final AlertDialog dig = builder.create();

                // 다이얼로그 출력
                dig.show();

                // 다이얼로그 크기를 위해 현재 화면 비율을 가져온다.
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                // 비율에 맞게 다이얼로그 크기를 지정
                Window window = dig.getWindow();

                // 가로 화면의 80% 세로 30%
                int x = (int) (size.x * 0.8f);
                int y = (int) (size.y * 0.3f);

                // 다이얼로그 크기 조정
                window.setLayout(x, y);


                // 다이얼로그 텍스트 변경
                content_tv.setText("존재하지 않는 아이디입니다");

                // 확인 버튼 클릭 리스너
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 다이얼로그 제거
                        dig.dismiss();
                    }
                });

            }
        }
    } // loginDB

    //------------------------------------------------------------------------------------------------------------------------------------
    //한글, 영어, 숫자를 제외한 문자열 삭제
    public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, "");
        return str;
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    public int Get_Internet()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return 1;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return 2;
            }
        }
        return 0;
    }
    //------------------------------------------------------------------------------------------------------------------------------------

}// LoginActivity
