package inno.i.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
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
import inno.i.Fragment.SecondFragment;
import inno.i.GlobalApplication;
import inno.i.R;

public class TestActivity extends AppCompatActivity {

    // 위도 경도를 담을 변수 static 이 코드 전체에 부담을 준다.
    // 가능 하면 지역 변수로 그리고 클래스 마다 간섭하지 못하게 하는것이 좋다.
    static double latitude;
    static double longitude;
    // 속도를 담을 변수
    static double speed;
    // 위치 제공자가 누군지를 담을 변수 (실내면 네트워크 실외면 gps) 이 값은 현재 테스트를 위해 운행 기록 데이터에 올라간다.
    static String status;
    static LocationManager lm;


    // 경로 하나의 정보를 임시로 담는 배열
    ArrayList<String> Course = new ArrayList<>();
    // 경로의 세부 정보 (0 :경로이름 1:경로 2:등/하원 )
    ArrayList<String[]> Course_data = new ArrayList<>();
    // 경로와 목적지를 매칭한 노선 정보
    ArrayList<String[]> Route_data = new ArrayList<>();


    // 경로 배열에서 다음위치 인덱스
    static int route_check;
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

    // 뒤로가기 버튼
    ImageButton btn_back;


    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btn_back = findViewById(R.id.main_titlebar_back);

        // 전역변수 객체 선언
        GlobalApplication myApp = (GlobalApplication) getApplication();
        GlobalApplication.setRoute_check(GlobalApplication.getRoute_check()+1);


        route_check= GlobalApplication.getRoute_check();
        Route_data = GlobalApplication.getSelected_route();


        GlobalApplication.setTest_check(1);//테스트 주행으로 변경


        // 전역 변수에 현재 목적지 저장
        myApp.setBeforelocation(nowlocation);
        Log.e("현재목적지", nowlocation);
        Log.e("현재목적지2",myApp.getBeforelocation());

        // 다음 목적지 변수에 다음 위치를 지정해준다
        next_location = Route_data.get(route_check)[1];
        // 지정한 변수를 전역변수에 저장한다.
        myApp.setNextlocation(next_location);
        Log.e("다음목적지", next_location);
        Log.e("다음목적지", myApp.getNextlocation());

        // 다다음 목적지 변수에 다다음 위치를 지정해준다
        try{
            next_location2 = Route_data.get(route_check+1)[1];
        }catch (Exception E){
            next_location2 = "운행종료";
        }
        // 지정한 변수를 전역변수에 저장한다.
        myApp.setNextNextlocation(next_location2);
        Log.e("다음다음목적지", next_location2);
        Log.e("다음다음목적지", myApp.getNextNextlocation());


        get_course();


        // 화면 전환 프래그먼트 선언 및 초기 화면 설정
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment, SecondFragment.newInstance(next_location)).commit();


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

                        Intent intent = new Intent(getApplicationContext(), SetActivity.class);

                        // 뒤로가기 버튼 클릭시 위치리스너 종료
                        lm.removeUpdates(mLocationListener);

                        // 테스트 주행 체크 변수 0으로 다시 변경
                        GlobalApplication.setTest_check(0);

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
    }
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

            Log.e("longitude>>",""+location.getLongitude());
            Log.e("latitude>>",""+location.getLatitude());

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


        route_check= GlobalApplication.getRoute_check();
        Log.e("route_check", route_check+"");
        nowlocation = Route_data.get(route_check-1)[1];



        if(route_check==Route_data.size()){
            return;
        }
        Log.e("1.route_check",""+route_check);
        Log.e("1-1.Routedata_latitude",Route_data.get(route_check)[2]);
        Log.e("1-2.Routedata_longitude",Route_data.get(route_check)[3]);
        Log.e("1-3.latitude",latitude+"");
        Log.e("1-4.longitude",longitude+"");
        Log.e("1-5.if문 result",""+ (Double.valueOf(Route_data.get(route_check)[2]) + 0.00050 > latitude && Double.valueOf(Route_data.get(route_check)[2]) - 0.00050 < latitude &&
                Double.valueOf(Route_data.get(route_check)[3]) + 0.00050 > longitude && Double.valueOf(Route_data.get(route_check)[3]) - 0.00050 < longitude));


        if (Double.valueOf(Route_data.get(route_check)[2]) + 0.00050 > latitude && Double.valueOf(Route_data.get(route_check)[2]) - 0.00050 < latitude &&
                Double.valueOf(Route_data.get(route_check)[3]) + 0.00050 > longitude && Double.valueOf(Route_data.get(route_check)[3]) - 0.00050 < longitude) {
            //if ((myApp.getSelected_type().trim()).equals("등원")) {
            Log.e("2.nowlocation", nowlocation);
            Log.e("3.다음 목적지", Route_data.get(route_check)[1]);
            Log.e("4.route_check", "" + route_check);
            Log.e("5.전체경로길이", "" + (Route_data.size() - 1));
            Log.e("6.다음 목적지",myApp.getNextlocation());
            if (Route_data.get(route_check)[1].equals(nowlocation)) {
                Log.e("요기는?","");
                return;
            }else {
                // 조건문 안에 들어오면 다이얼로그 플래그를 1로 바꾼다.
                if (!TestActivity.this.isFinishing() && speed <= 5 && dialog_flag == 0) {
                    Log.e("여기 들어오나?","");
                    // 조건문 안에 들어오면 다이얼로그 플래그를 1로 바꾼다.
                    dialog_flag = 1;

                    Log.e("--------ArriveActivity","--------");
                    Log.e("1.route_check", ""+route_check);
                    Log.e("2.length",""+Route_data.size());
                    if(route_check==Route_data.size()-1){
                        Log.e("3.route_check", ""+route_check);
                        Log.e("4.length",""+Route_data.size());

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


                        // 다이얼로그 텍스트 변경
                        textView2.setText("최종 목적지에 도착했습니다. 운행을 종료하시겠습니까?");
                        // 확인 버튼 클릭 리스너
                        button2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 최종 목적지 도착 시
                                Intent intent = new Intent(getApplicationContext(), SetActivity.class);
                                // 뒤로가기 버튼 클릭시 위치리스너 종료
                                lm.removeUpdates(mLocationListener);

                                // 전역변수 초기화 후 운행종료
                                myApp.init_before_start();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                                startActivity(intent);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                // 테스트 주행 체크 변수 0으로 다시 변경
                                GlobalApplication.setTest_check(0);
                                //check_getoff = 1;
                                nowlocation = "최종목적지도착";
                                // 다이얼로그 제거
                                dig2.dismiss();

                            }
                        });

                    }else if(route_check!=Route_data.size()-1){
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


                                Log.e("6.route_check", ">>" + GlobalApplication.getRoute_check() + ">>" + route_check);

                                // 전역 변수에 변경된 현재, 다음 목적지 저장
                                myApp.setBeforelocation(nowlocation);
                                myApp.setNextlocation(Route_data.get(route_check)[1]);

                                // 화면 전환 프래그먼트 선언 및 초기 화면 설정
                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.add(R.id.fragment, SecondFragment.newInstance(myApp.getNextlocation())).commit();

                                Log.e("7.nowlocation", myApp.getBeforelocation());
                                Log.e("8.nextlocation", myApp.getNextlocation());




                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });
                    }
                }

            }
            //} else {//하원

            //}



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

                Intent intent = new Intent(getApplicationContext(), SetActivity.class);

                // 뒤로가기 버튼 클릭시 위치리스너 종료
                lm.removeUpdates(mLocationListener);
                // 테스트 주행 체크 변수 0으로 다시 변경
                myApp.setTest_check(0);
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
