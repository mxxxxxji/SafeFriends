package inno.i.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import inno.i.Fragment.AttendFragment;


import inno.i.Fragment.SecondFragment;
import inno.i.GlobalApplication;
import inno.i.MyPagerAdapter;
import inno.i.R;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {


    // 위도 경도를 담을 변수 static 이 코드 전체에 부담을 준다.
    // 가능 하면 지역 변수로 그리고 클래스 마다 간섭하지 못하게 하는것이 좋다.
    static double latitude;
    static double longitude;
    // 속도를 담을 변수
    static double speed;
    // 위치 제공자가 누군지를 담을 변수 (실내면 네트워크 실외면 gps) 이 값은 현재 테스트를 위해 운행 기록 데이터에 올라간다.
    static String status;
    static LocationManager lm;



    // 승차 예정인원과 현재 인원을 나타내는 텍스트뷰
    TextView childnum_textView;
    // 하단 바 텍스트뷰 구성
    TextView person_tv;
    // 세팅 버튼 현재 미사용
    ImageButton btn_setting;
    ImageButton btn_back;
    // 뷰페이저 선언
    // 프래그먼트를 보다 효율적으로 사용하게 한다. (슬라이드 형식)
    MyPagerAdapter adapterViewPager;
    ViewPager vpPager;


    // 경로 하나의 정보를 임시로 담는 배열
    ArrayList<String> Course = new ArrayList<>();
    // 경로의 세부 정보 (0 :경로이름 1:경로 2:등/하원 )
    ArrayList<String[]> Course_data = new ArrayList<>();
    // 경로와 목적지를 매칭한 노선 정보
    ArrayList<String[]> Route_data = new ArrayList<>();
    int route_stauts[] = new int[30];

    // 경로 배열에서 다음위치 인덱스
    static int route_check;
    // 차량의 운행상태를 체크하기 위한 변수
    static int vehicle_check;
    // 해당 지역에 도착 했을때 다이얼로그가 팝업 되어서 다이얼로그가 중첩된다. 그걸 방지 하기 위한 플래그 값
    // 0이면 팝업 되며 1이면 팝업 되지 않는다.
    static int dialog_flag = 0;
    // 다이얼로그 빌더를 위한 액티비티 컨텍스트
    final Context context = this;
    // 현 위치를 저장하는 변수 초기 값은 "출발지역"으로 한다.
    static String nowlocation ="출발지역";
    // 다음 위치를 저장하는 변수
    static String next_location;
    // 다다음 위치를 저장하는 변수 (현 앱이 다다음까지 뷰페이져로 나타낸다.)
    static String next_location2;


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        // 전역변수 객체 선언
        GlobalApplication myApp = (GlobalApplication) getApplication();
        route_check= GlobalApplication.getRoute_check();
        Route_data = GlobalApplication.getSelected_route();
        int start_check = GlobalApplication.getStart_check();




        get_course();

        //차량의 운행상태를 0으로 변경(0:정지 1:운행중)
        GlobalApplication.setVehicle_check(1);


        // 세팅 버튼
        btn_setting = findViewById(R.id.main_titlebar_settiong);
        btn_back = findViewById(R.id.main_titlebar_back);
        // 승차인원/현재인원 바
        person_tv = findViewById(R.id.tv_getonnum);


        if(GlobalApplication.getSelected_type().equals("등원")) {
            person_tv.setText("출석현황 " + GlobalApplication.getTotal_child_num() + "명 | 출석예정 " + 0 + "명");
        }else{
            person_tv.setText("하차현황 " + GlobalApplication.getTotal_child_num() + "명 | 하차예정 " + 0 + "명");
        }

        try{
            // 전역 변수에 현재 목적지 저장
            myApp.setBeforelocation(nowlocation);
            // 다음 목적지 변수에 다음 위치를 지정해준다
            next_location = Route_data.get(route_check)[1];
            // 지정한 변수를 전역변수에 저장한다.
            myApp.setNextlocation(next_location);
            // 다다음 목적지 변수에 다다음 위치를 지정해준다
            try{
                next_location2 = Route_data.get(route_check+1)[1];
            }catch (Exception E){
                next_location2 = "운행종료";
            }
            // 지정한 변수를 전역변수에 저장한다.
            myApp.setNextNextlocation(next_location2);

        }catch(IndexOutOfBoundsException e){//목적지가 하나만 등록된 경우 팝업창 띄우고 종료

            // 다이얼로그 빌더 선언
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
            textView.setText("등록된 목적지가 1개 입니다.\n2개이상 등록해주세요");

            // 확인 버튼 클릭 리스너
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GlobalApplication myApp = (GlobalApplication) getApplication();
                    GlobalApplication.setThread(false);

                    Intent intent = new Intent(getApplicationContext(), StartActivity.class);

                    // 위치리스너 종료
                    lm.removeUpdates(mLocationListener);

                    // 전역변수 초기화 후 운행종료
                    myApp.init_before_start();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    // 다이얼로그 제거
                    dig.dismiss();
                }
            });

        }


        vpPager = findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        // 어댑터 적용
        vpPager.setAdapter(adapterViewPager);
        // 뷰페이져 시작 시 화면에 뜰 포지션
        vpPager.setCurrentItem(1);
        // 뷰페이져 인티케이터 선언
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);


        //차량의 운행상태를 0으로 변경(0:정지 1:운행중)
        GlobalApplication.setVehicle_check(1);



        // 현 위치를 저장하는 위치 리스너
        // LocationManager 객체를 얻어온다
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 사용자의 위치제공 권한 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // GPS 위치 제공자
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                500, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        // Network 위치 제공자
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                500, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);



        // 세팅 버튼 클릭 리스너
        btn_setting.setOnClickListener(new View.OnClickListener() {
            // 취소 버튼 이벤트
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // 페이드인,아웃 효과 설정
            }
        });
        // 뒤로가기 버튼 리스너
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                textView.setText("운행을 종료하시겠습니까?");

                // 확인 버튼 클릭 리스너
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        GlobalApplication myApp = (GlobalApplication) getApplication();
                        GlobalApplication.setThread(false);

                        Intent intent = new Intent(getApplicationContext(), StartActivity.class);

                        // 뒤로가기 버튼 클릭시 위치리스너 종료
                        lm.removeUpdates(mLocationListener);

                        // 전역변수 초기화 후 운행종료
                        myApp.init_before_start();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        // 다이얼로그 제거
                        dig.dismiss();
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
        });
        if(GlobalApplication.getSelected_type().equals("하원") && dialog_flag==0 && start_check==0){
            dialog_flag=1;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(context);

            // 다이얼로그 레이아웃 ui 선언
            View view = inflater.inflate(R.layout.popup_exit, null);

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
            textView.setText("탑승 인원을 확인하세요");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 다시 다이얼로그 플래그를 0으로 한다.
                    dialog_flag = 0;

                    vpPager.setCurrentItem(0);
                    adapterViewPager.notifyDataSetChanged();
                    // 화면 전환 효과
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    // 다이얼로그 제거
                    dig.dismiss();
                }
            });

        }

    }//end of onCreate


    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 위치 리스너 선언
    protected final LocationListener mLocationListener = new LocationListener() {

        //여기서 위치값이 갱신되면 이벤트가 발생한다.
        //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            status = location.getProvider(); // 제공자
            speed = location.getSpeed(); // 속도

            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            // 전역변수 객체 선언
            GlobalApplication myApp = (GlobalApplication) getApplication();

            // 현재 경도 위도를 전역 변수에 저장한다
            GlobalApplication.setLongitude(longitude);
            GlobalApplication.setLatitude(latitude);

            // 현 경/위도 에 대한 이벤트 함수
            location_chk(latitude, longitude);

        }

        // 리스너 제공자 변경 및 이벤트 수행 유무
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    void location_chk(Double latitude, Double longitude){
        // 함수가 호출될때 변하지 않을 전역 객체를 불러온다
        final GlobalApplication myApp = (GlobalApplication) getApplication();

        route_stauts= GlobalApplication.getRoute_status();
        route_check= GlobalApplication.getRoute_check();
        nowlocation = Route_data.get(route_check-1)[1];

        for(int i=0; i<5; i++){
            Log.e("route_status["+i+"]",route_stauts[i]+"");
        }
        //다음 목적지에 내릴 학생이 없다면
        if(route_stauts[route_check]==0 && GlobalApplication.getLongclick_flag()!=0){

            GlobalApplication.setRoute_check(GlobalApplication.getRoute_check() + 1);
            route_check = GlobalApplication.getRoute_check();
            if (route_check > Route_data.size() - 1) {
                nowlocation = "최종목적지도착";
            } else {
                nowlocation = Route_data.get(route_check - 1)[1];

            }
            // 전역 변수에 변경된 현재, 다음 목적지 저장
            myApp.setBeforelocation(nowlocation);
            myApp.setNextlocation(Route_data.get(route_check)[1]);
            adapterViewPager.notifyDataSetChanged();
        }
        if(route_check==Route_data.size()){
            return;
        }

        if (Double.valueOf(Route_data.get(route_check)[2]) + 0.00050 > latitude && Double.valueOf(Route_data.get(route_check)[2]) - 0.00050 < latitude &&
                Double.valueOf(Route_data.get(route_check)[3]) + 0.00050 > longitude && Double.valueOf(Route_data.get(route_check)[3]) - 0.00050 < longitude) {

                if (Route_data.get(route_check)[1].equals(nowlocation)) {
                    return;
                }else {
                    // 조건문 안에 들어오면 다이얼로그 플래그를 1로 바꾼다.
                    if (!MainActivity.this.isFinishing() && speed <= 5 && dialog_flag == 0) {
                        // 조건문 안에 들어오면 다이얼로그 플래그를 1로 바꾼다.
                        dialog_flag = 1;

                        if(route_check==Route_data.size()-1){
                            // 다이얼로그 빌더 선언
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                            LayoutInflater inflater2 = LayoutInflater.from(context);

                            // 다이얼로그 레이아웃 ui 선언
                            View view2 = inflater2.inflate(R.layout.popup_confirm, null);

                            // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                            Button button2 = view2.findViewById(R.id.btn_confirm);
                            TextView textView2 = view2.findViewById(R.id.tv_content);

                            // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                            builder2.setCancelable(false);

                            // 빌더 레이아웃 연결
                            builder2.setView(view2);

                            // 다이얼로그 생성
                            final AlertDialog dig2 = builder2.create();

                            // 다이얼로그 출력
                            dig2.show();

                            // 다이얼로그 크기를 위해 현재 화면 비율을 가져온다.
                            Display display2 = getWindowManager().getDefaultDisplay();
                            Point size2 = new Point();
                            display2.getSize(size2);

                            // 비율에 맞게 다이얼로그 크기를 지정
                            Window window2 = dig2.getWindow();

                            // 가로 화면의 80% 세로 30%
                            int x2 = (int) (size2.x * 0.8f);
                            int y2 = (int) (size2.y * 0.3f);

                            // 다이얼로그 크기 조정
                            window2.setLayout(x2, y2);

                            if(GlobalApplication.getSelected_type().equals("등원")){
                                // 다이얼로그 텍스트 변경
                                textView2.setText("최종 목적지에 도착했습니다 하차 진행해주세요.");
                                // 확인 버튼 클릭 리스너
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        vpPager.setCurrentItem(0);

                                        // 최종 목적지 도착 시에는 플래그 값을 넘겨주어 기존 출석체크와는 다른 하차처리가 발생하도록 한다.
                                        AttendFragment.newInstance2(1);
                                        nowlocation = "최종목적지도착";
                                        // 다이얼로그 제거
                                        dig2.dismiss();

                                    }
                                });
                            }else{
                                // 다이얼로그 텍스트 변경
                                textView2.setText("최종 목적지에 도착했습니다");
                                button2.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // 모든 인원 하차가 완료 되었으니 액티비티를 전환한다
                                        Intent intent = new Intent(getApplicationContext(), GetoffActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                                        startActivity(intent);
                                        finish();
                                       overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    }
                                });

                            }

                        }else if(route_check!=Route_data.size()-1 && route_stauts[route_check]!=0){
                            myApp.setNextlocation(Route_data.get(route_check)[1]);
                            // 다이얼로그 빌더 선언
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
                            textView.setText("해당 지역에 도착했습니다. 정차해 주세요");

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 다시 다이얼로그 플래그를 0으로 한다.
                                    dialog_flag = 0;

                                    GlobalApplication.setRoute_check(GlobalApplication.getRoute_check() + 1);
                                    route_check = GlobalApplication.getRoute_check();
                                    if (route_check > Route_data.size() - 1) {
                                        nowlocation = "최종목적지도착";
                                    } else {
                                        nowlocation = Route_data.get(route_check - 1)[1];

                                    }

                                    // 전역 변수에 변경된 현재, 다음 목적지 저장
                                    myApp.setBeforelocation(nowlocation);
                                    myApp.setNextlocation(Route_data.get(route_check)[1]);


                                    vpPager.setCurrentItem(0);
                                    adapterViewPager.notifyDataSetChanged();

                                    // 다이얼로그 제거
                                    dig.dismiss();
                                }
                            });
                        }
                    }

                }
        }
    }



    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void get_course(){
        String course_info;
        GlobalApplication myApp = (GlobalApplication) getApplication();
        course_info = GlobalApplication.getCourse();
        Course.addAll(Arrays.asList(course_info.split("<br>")));
        for (int i = 0; i < Course.size(); i++) {
            Course_data.add(Course.get(i).split(","));
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 현 화면 텍스트뷰를 변경한다
    public void changeText(String text) {
        person_tv.setText(text);
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
        textView.setText("운행을 종료하시겠습니까?");

        // 확인 버튼 클릭 리스너
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalApplication myApp = (GlobalApplication) getApplication();
                GlobalApplication.setThread(false);

                Intent intent = new Intent(getApplicationContext(), StartActivity.class);

                // 뒤로가기 버튼 클릭시 위치리스너 종료
                lm.removeUpdates(mLocationListener);

                // 전역변수 초기화 후 운행종료
                myApp.init_before_start();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                // 다이얼로그 제거
                dig.dismiss();
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
