package inno.i.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import inno.i.Fragment.AttendFragment;
import inno.i.Fragment.SeatingFragment;
import inno.i.GlobalApplication;
import inno.i.R;
import me.relex.circleindicator.CircleIndicator;

public class ArriveActivity extends AppCompatActivity {

    // 하단 바 텍스트뷰 구성
    TextView person_tv;
    // 이름과 학급을 나타내는 텍스트뷰
    TextView vehicle_textView;
    TextView name_textView;
    TextView class_textView;
    TextView vehicle_num_textView;
    // 뒤로가기 이미지 버튼
    ImageButton arv_cancel;


    // 뷰페이져 어댑터 정의
    MyPagerAdapter1 adapterViewPager;
    // 뷰페이져의 인디케이터 위치
    int position;

    // 현 액티비티의 컨텍스트 정의
    final Context context = this;


    // 현재 경로 위치
    static int length;
    // 경로 배열에서 다음위치 인덱스
    static int route_check;

    //------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrive);

        route_check = GlobalApplication.getRoute_check();//다음 위치 인덱스 가져오기
        length=getIntent().getIntExtra("length",0); //
        position = getIntent().getIntExtra("position", 0);


        // 하단 바 ui 연결
        vehicle_textView = findViewById(R.id.ArriveActivity_textView_vehicle);
        name_textView = findViewById(R.id.ArriveActivity_textView_name);
        class_textView = findViewById(R.id.ArriveActivity_textView_class);
        vehicle_num_textView = findViewById(R.id.ArriveActivity_textView_vehicle_num);
        person_tv = findViewById(R.id.arriveactivity_textview_people_num);
        // 타이틀 바 ui 연결
        arv_cancel = findViewById(R.id.main_titlebar_back);


        // 화면에 텍스트 출력
        vehicle_textView.setText(GlobalApplication.getSelected_vehicle().get(1));                   //차량명
        vehicle_num_textView.setText("("+ GlobalApplication.getSelected_vehicle().get(2)+") | ");    //차량번호
        name_textView.setText(GlobalApplication.getSelected_teacher().get(0));                      //선생님이름
        class_textView.setText("("+ GlobalApplication.getSelected_teacher().get(1) + ")");          //선생님반


        // 뷰페이져 ui 연결
        ViewPager vpPager = findViewById(R.id.vpPager);
        // 뷰페이져 어댑터 선언한다, 도착 액티비티 프래그먼트를 인자로 넘겨줌
        adapterViewPager = new MyPagerAdapter1(getSupportFragmentManager());//getSupportFragmentManager()
        // 어댑터 적용
        vpPager.setAdapter(adapterViewPager);
        // 뷰페이져 시작 시 화면에 뜰 포지션
        vpPager.setCurrentItem(position);
        // 뷰페이져 인티케이터 선언
        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);



        // 취소 버튼 이벤트
        arv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GlobalApplication myApp = (GlobalApplication) getApplication();

/*                // 만약 좌석에 벨트가 풀려있는 상태면 해당 화면을 나갈 수 없다
                if (myApp.getResid() == R.drawable.icon_seat_belt_off) {
                    return;

                } else {
*/
                    // 벨트가 연결 되있으면
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거

                    // 도착 액티비티를 떠나기전
                    // status 값을 0으로 바꾼다
//                    myApp.setStatus(0);

                    // 다음 목적지를 메인 액티비티에 보내준다.
                    //intent.putExtra("nowlocation", now_location);

                    // 현 액티비티 종료

                    startActivity(intent);
                    finish();
                    // 페이드인,아웃 효과 설정
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                }
            }
        });

        if(route_check>length-1){

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
            textView.setText("최종 목적지에 도착했습니다 하차 진행해주세요.");
            // 확인 버튼 클릭 리스너
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 최종 목적지 도착 시에는 플래그 값을 넘겨주어 기존 출석체크와는 다른 하차처리가 발생하도록 한다.
                    AttendFragment.newInstance2(1);

                    // 다이얼로그 제거
                    dig.dismiss();

                }
            });
        }

    }//end of onCreate

    //-----------------------------------------------------------------------------------------------
    // 뷰페이져 어댑터 클래스
    public static class MyPagerAdapter1 extends FragmentPagerAdapter {
        // 총 페이지 2개
        private static int NUM_ITEMS = 2;

        // 부모 생성자 선언
        public MyPagerAdapter1(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // 총 페이지 수 리턴
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // 해당 페이지에 표시할 프래그먼트 반환
        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    // 좌석 프래그먼트에 모듈 수를 넘겨 준다
                    return SeatingFragment.newInstance(1);
                case 1:
                    return AttendFragment.newInstance();
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }

    //-----------------------------------------------------------------------------------------------
    // 현 화면 텍스트뷰를 변경한다
    public void changeText(String text) {
        person_tv.setText(text);
    }
}
