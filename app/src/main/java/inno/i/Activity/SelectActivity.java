package inno.i.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.estimote.sdk.cloud.internal.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import inno.i.GlobalApplication;
import inno.i.R;
import inno.i.SpinnerAdapter.SpinnerAdpater1;

public class SelectActivity extends AppCompatActivity {

    // 차량 정보가 담긴 배열 동적 할당
    ArrayList<String> Vehicle= new ArrayList<>();
    // 목적지의 세부 정보 (0 : 이름 1: 차번호 2:좌석수)
    ArrayList<String[]> Vehicle_data = new ArrayList<>();
    // 선생님 정보가 담긴 배열 동적 할당
    ArrayList<String> Teacher = new ArrayList<>();
    // 선생님의 세부 정보
    ArrayList<String[]> Teacher_data = new ArrayList<>();
    // 경로 하나의 정보를 임시로 담는 배열
    ArrayList<String> Course = new ArrayList<>();
    // 경로의 세부 정보 (0 :경로이름 1:경로 2:등/하원 )
    ArrayList<String[]> Course_data = new ArrayList<>();
    // 비어있는 정보가 몇 개인지 확인 하기위한 변수
    int info_count=0;
    // 정보 확인
    static int[] info_check;
    // 0이면 팝업 되며 1이면 팝업 되지 않는다.
    static int dialog_flag = 0;
    // 다이얼로그 빌더를 위한 액티비티 컨텍스트
    final Context context = this;

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 전체화면
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        GlobalApplication myApp = (GlobalApplication) getApplication();
        //UI생성
        //TextView textview_teacher = findViewById(R.id.SelectActivity_spinner_teacher);
        //선생님 Spinner를 Textview로 변경해둠..바꿔야함..
        final Spinner spinner_teacher = findViewById(R.id.SelectActivity_spinner_teacher);
        final Spinner spinner_vehicle = findViewById(R.id.SelectActivity_spinner_vehicle);
        final RadioGroup radioGroupg = findViewById(R.id.SelectActivity_radioGroup);
        RadioButton radio_go = findViewById(R.id.SelectActivity_radioButton_goto);
        Button button_start = findViewById(R.id.SelectActivity_button_start);

        // 버튼 등원에 눌려져 있기
        radioGroupg.check(radio_go.getId());


        // 차량 정보를 불러온다
        // 선생님 정보를 불러온다
        get_vehicle();
        get_course();


        //운행시작버튼
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getCheckedRadioButtonId() 의 리턴값은 선택된 RadioButton 의 id 값.
                int id = radioGroupg.getCheckedRadioButtonId();

                RadioButton selected_radioButton = findViewById(id);

                //비어있는 정보의 수가 0일 때
                //선택된 라디오 버튼
                if (info_count == 0) {
                    //버튼의 텍스트 값 저장
                    String type = selected_radioButton.getText().toString();
                    //선택된 유형 저장
                    GlobalApplication.setSelected_type(type.trim());
                    Log.e(">>" + GlobalApplication.getSelected_type().trim(), ">>" + (GlobalApplication.getSelected_type().trim()).length());
                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                    startActivity(intent);

                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                }else if(info_count == 1){//비어있는 정보의 수가 하나일 경우
                    Log.e("info_count","1");

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);

                    // 다이얼로그 레이아웃 ui 선언
                    View view = inflater.inflate(R.layout.popup_confirm, null);

                    // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                    Button button = view.findViewById(R.id.btn_confirm);
                    TextView textView = view.findViewById(R.id.tv_content);

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
                    if(info_check[0]==6){
                        textView.setText("운전자가 배정되지 않았습니다");
                    }else if(info_check[0]==8){
                        textView.setText("등원경로가 배정되지 않았습니다");
                    }else{
                        textView.setText("하원경로가 배정되지 않았습니다");
                    }

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 다시 다이얼로그 플래그를 0으로 한다.
                            dialog_flag = 0;

                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });

                }else if(info_count == 2){//등원경로 정보가 없는 경우
                    Log.e("info_count", "2");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);

                    // 다이얼로그 레이아웃 ui 선언
                    View view = inflater.inflate(R.layout.popup_confirm, null);

                    // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                    Button button = view.findViewById(R.id.btn_confirm);
                    TextView textView = view.findViewById(R.id.tv_content);

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
                    if(info_check[0]==6 && info_check[1]==8){
                        textView.setText("운전자, 등원경로가 \n배정되지 않았습니다");
                    }else if(info_check[0]==6 && info_check[1]==9){
                        textView.setText("운전자, 하원경로가 \n배정되지 않았습니다");
                    }else{
                        textView.setText("등원경로, 하원경로가 \n배정되지 않았습니다");
                    }



                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 다시 다이얼로그 플래그를 0으로 한다.
                            dialog_flag = 0;

                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });
                }else {//하원경로 정보가 없는 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);

                    // 다이얼로그 레이아웃 ui 선언
                    View view = inflater.inflate(R.layout.popup_confirm, null);

                    // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                    Button button = view.findViewById(R.id.btn_confirm);
                    TextView textView = view.findViewById(R.id.tv_content);

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
                    textView.setText("운전자, 등원경로, 하원경로가\n배정되지 않았습니다");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 다시 다이얼로그 플래그를 0으로 한다.
                            dialog_flag = 0;
                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });
                }
            } // end onClick()
        });  // end Listener



        //차량 스피너 adapter
        SpinnerAdpater1 vehicle_spinner_adpt = new SpinnerAdpater1(getApplicationContext(), Vehicle_data, "vehicle");
        //Adapter 적용
        spinner_vehicle.setAdapter(vehicle_spinner_adpt);
        spinner_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view2, int position, long id) {
                    Log.e("누를 때마다", "들어오는걸가?");
                    //초기화
                    info_check = new int[3];
                    for(int i=0; i<3; i++){
                        info_check[i] =0;
                    }
                    info_count=0;
                    //선택된 차량정보를 GlobalApplication에 넣음
                    ArrayList<String> info = new ArrayList<>();
                    for (int i = 0; i < Vehicle_data.get(0).length; i++){
                        if("선택없음".equals(Vehicle_data.get(position)[i])){
                            //정보가 없는 곳의 인덱스를 저장(0은 정보가다 있는 경우이므로 +1해서 저장)
                            info_check[info_count] = i+1;
                            info_count++;
                        }else{
                            info.add(Vehicle_data.get(position)[i]);
                        }
                    }
                    GlobalApplication.setSelected_vehicle(info);
                    for(int i=0; i<3; i++)
                        Log.e("info_check", ""+info_check[i]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // 펌웨어 조건
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 스피너의 드랍다운 위치 조정
            spinner_vehicle.setDropDownVerticalOffset(180);
        }


        //선택된 선생님 전역변수에 사용자의 정보를 넣는다
        Teacher.addAll(Arrays.asList(GlobalApplication.getUser().split(",")));
        GlobalApplication.setSelected_teacher(Teacher);
        //스피너에 넣을 선생님데이터에 사용자의 정보를 넣는다
        Teacher_data.add(GlobalApplication.getUser().split(","));

        //선생님 스피너 adapter
        SpinnerAdpater1 teacher_spinner_adpt = new SpinnerAdpater1(getApplicationContext(), Teacher_data, "teacher");
        //Adapter 적용
        spinner_teacher.setAdapter(teacher_spinner_adpt);
        spinner_teacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //선택된 선생님 정보를 GlobalApplication에 넣음
                //현재 선생님아이디로 접속하므로 이 부분은 주석처리
            /*    ArrayList<String> info = new ArrayList<>();
                for(int i=0; i<2; i++){ //spinner의 정보이므로 선생님 이름, 반 이름만 적혀잇음
                    info.add(Teacher_data.get(position)[i]);
                }
                GlobalApplication.setSelected_teacher(info);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // 펌웨어 조건
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 스피너의 드랍다운 위치 조정
            spinner_teacher.setDropDownVerticalOffset(180);
        }


    }
    //-------------------------------------------------------------------------------------------------------------------
    public void get_vehicle() {
        String vehicle_info;
            vehicle_info = GlobalApplication.getVehicle();
            Vehicle.addAll(Arrays.asList(vehicle_info.split("<br>")));
            for (int i = 0; i < Vehicle.size(); i++){
                Vehicle_data.add(Vehicle.get(i).split(","));
            }

    }

    //-------------------------------------------------------------------------------------------------------------------
    public void get_course(){
        String course_info;
        GlobalApplication myApp = (GlobalApplication) getApplication();
        course_info = GlobalApplication.getCourse();
        Log.i(course_info, "course_info");
        Course.addAll(Arrays.asList(course_info.split("<br>")));
        for (int i = 0; i < Course.size(); i++){
            Course_data.add(Course.get(i).split(","));
        }
    }

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {

        // 다이얼로그 빌더 선언
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        // 다이얼로그 레이아웃 ui 선언
        View view = inflater.inflate(R.layout.popup_exit, null);

        // 다이얼로그 버튼 및 텍스트뷰 ui 연결
        Button button = view.findViewById(R.id.btn_confirm);
        Button button_cancel = view.findViewById(R.id.btn_cancel);
        TextView textView = view.findViewById(R.id.tv_content);

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
        textView.setText("어플리케이션을 종료하시겠습니까?");

        // 확인 버튼 클릭 리스너
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 제거
                dig.dismiss();
            }
        });
    }


}
