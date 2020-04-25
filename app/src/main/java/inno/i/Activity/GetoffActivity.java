package inno.i.Activity;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import inno.i.Fragment.GetoffFragment;
import inno.i.GlobalApplication;
import inno.i.R;
import inno.i.SpinnerAdapter.SpinnerAdapter3;
import me.relex.circleindicator.CircleIndicator;

public class GetoffActivity extends AppCompatActivity {

    //UI 스피너 선언
    Spinner spinner2;
    //스피너 어댑터 선언
    SpinnerAdapter3 adapterSpinner2;
    // 뷰페이저 선언
    // 프래그먼트를 보다 효율적으로 사용하게 한다. (슬라이드 형식)
    MyPagerAdapter adapterViewPager;


    // 이름과 학급을 나타내는 텍스트뷰
    TextView vehicle_textView;
    TextView name_textView;
    TextView class_textView;
    TextView vehicle_num_textView;
    //뒤로가기버튼
    ImageButton btn_back;
    //설정버튼
    ImageButton btn_setting;

    //-----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getoff);

        GlobalApplication myApp = (GlobalApplication) getApplication();

        // 하단 바
        vehicle_textView = findViewById(R.id.GetoffActivity_textView_vehicle);
        name_textView = findViewById(R.id.GetoffActivity_textView_name);
        class_textView = findViewById(R.id.GetoffActivity_textView_class);
        vehicle_num_textView = findViewById(R.id.GetoffActivity_textView_vehicle_num);


        // 화면에 텍스트 출력
        vehicle_textView.setText(GlobalApplication.getSelected_vehicle().get(1));                   //차량명
        vehicle_num_textView.setText("("+ GlobalApplication.getSelected_vehicle().get(2)+") | ");    //차량번호
        name_textView.setText(GlobalApplication.getSelected_teacher().get(0));                      //선생님이름
        class_textView.setText("("+ GlobalApplication.getSelected_teacher().get(1) + ")");          //선생님반


        btn_back = findViewById(R.id.getoff_titlebar_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        btn_setting = findViewById(R.id.getoff_titlebar_settiong);
        btn_setting.setOnClickListener(new View.OnClickListener() {
            // 취소 버튼 이벤트
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getApplicationContext(), SetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                // 페이드인,아웃 효과 설정*/
            }
        });


        // 스피너에 들어갈 어린이들이 담긴 어레이 리스트 선언
        List<String> children = new ArrayList<>();
        children.add("하차완료");
        // 스피너 ui 연결
        spinner2 = findViewById(R.id.spinner2);
        //스피너 Adapter 선언, 어린이 리스트를 인자값으로 보내준다.
        adapterSpinner2 = new SpinnerAdapter3(getApplicationContext(), children);
        //  Adapter 적용
        spinner2.setAdapter(adapterSpinner2);



        // 뷰페이저 ui 연결
        ViewPager vpPager = findViewById(R.id.vpPager);
        // 뷰페이저를 선언 하고 프래그먼트와 연결
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        // 뷰페이저 Adapter 적용
        vpPager.setAdapter(adapterViewPager);
        // 뷰페이져 처음 화면을 가운데 페이지로 지정
        vpPager.setCurrentItem(1);
        // 뷰페이저 하단에 들어가는  인디케이터 ui 연결
        CircleIndicator indicator = findViewById(R.id.indicator);
        // 인디케이터 적용
        indicator.setViewPager(vpPager);

    }//end of onCreate

    //-----------------------------------------------------------------------------------------------
    // 뷰페이저 어탭터
    public static class MyPagerAdapter extends FragmentPagerAdapter {
        // 페이지 수 3개
        private static int NUM_ITEMS = 3;

        // 부모 생성자
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // 페이즈 총 수 반환
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // 해당 페이지에 표시할 프래그먼트 반환
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return GetoffFragment.newInstance();
                case 1:
                    return GetoffFragment.newInstance();
                case 2:
                    return GetoffFragment.newInstance();
                default:
                    return null;
            }
        }
    }

    //-----------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
