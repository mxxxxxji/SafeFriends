package inno.i.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import inno.i.Activity.MainActivity;
import inno.i.GlobalApplication;
import inno.i.R;
import inno.i.SpinnerAdapter.SpinnerAdapter2;
import inno.i.SpinnerAdapter.SpinnerAdapter3;

public class SecondFragment extends Fragment {

    private Activity activity;


    static String next_location;


    //UI 스피너 선언
    Spinner spinner1;
    Spinner spinner2;
    //스피너 어댑터 선언
    SpinnerAdapter2 adapterSpinner1;
    SpinnerAdapter3 adapterSpinner2;


    // 선택된 차량, 경로에 맞는 아이들의 세부정보( 0:학생ID, 1:반ID, 2:이름, 3:성별, 4:생일, 5:부모님, 6:연락처, 7:주소, 8:반이름, 9:사진URL, 10:등원장소, 11:하원장소, 12:NFC데이터)
    ArrayList<String[]> Selected_student = new ArrayList<>();
    // 경로와 목적지를 매칭한 노선 정보
    ArrayList<String[]> Route_data = new ArrayList<>();

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public static SecondFragment newInstance(String next_location1) {
        SecondFragment fragment = new SecondFragment();
        // 부모 액티비티 에서 다음 목적지 값을 받아온다
        next_location = next_location1;
        return fragment;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 프래그먼트가 액티비티와 연결 됬을때
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            // 상속 액티비티를 선언
            activity = (Activity) context;
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 프래그먼트 xml 연결
        View view = inflater.inflate(R.layout.viewpager2, container, false);

        GlobalApplication myApp = new GlobalApplication();
        Route_data = GlobalApplication.getSelected_route();
        next_location=myApp.getNextlocation();
        Selected_student = GlobalApplication.getSelected_student();

        // 텍스트뷰 xml 연결
        TextView locationview2 = view.findViewById(R.id.destination_View);

        Log.e("텍스트뷰 값 입력++++++++++++++", next_location);
        // 텍스트뷰 값 입력
        locationview2.setText(next_location);

        // 스피너 ui 연결
        spinner1 = view.findViewById(R.id.spinner1);
        //스피너 Adapter 선언, 목적지 리스트와 다음위치를 인자값으로 보내준다.
        adapterSpinner1 = new SpinnerAdapter2(getContext(), Route_data);
        // 펌웨어 조건
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // 스피너의 드랍다운 위치 조정
            spinner1.setDropDownVerticalOffset(225);
        }
        //  Adapter 적용
        spinner1.setAdapter(adapterSpinner1);



        if(GlobalApplication.getTest_check()==0){//테스트 주행일 경우
            // 스피너에 들어갈 어린이들이 담긴 어레이 리스트 선언
            List<String> children = new ArrayList<>();

            Log.e("--------","<<SecondFragment>>");
//            Log.e("1.Route_data.size()", ""+Route_data.size());
            // 목적지 마다
            for (int i = 0; i < Route_data.size(); i++) {
                // 해당 되는 아이들에
                for (int j = 0; j < Selected_student.size(); j++) {

                    // 어린이 승/하자 지점을 temp에 담고
                    String temp = Selected_student.get(j)[10];

 //                   Log.e("2.Path.get("+i+")", Route_data.get(i)[1]);
 //                   Log.e("3.temp", temp);

                    // 목적지 == 어린이 승/하차 지점 일때
                    if ((StringReplace(Route_data.get(i)[1])).equals(temp)) {
                        Log.e( "4.result",""+( ( StringReplace( Route_data.get(i)[1] ) ).equals(temp) ) );
                        // 스피너에 들어갈 어린이 리스트에 추가해준다.
                        children.add(Selected_student.get(j)[2]);

                    }
                }
            }

            for(int i=0; i<children.size(); i++)
   //             Log.i("child"+"["+i+"]", children.get(i));

            // 스피너 ui 연결
            spinner2 = view.findViewById(R. id.spinner2);
            //스피너 Adapter 선언, 어린이 리스트를 인자값으로 보내준다.
            adapterSpinner2 = new SpinnerAdapter3(getContext(), children);
            // 펌웨어 조건
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // 스피너의 드랍다운 위치 조정
                spinner2.setDropDownVerticalOffset(225);
            }
            //  Adapter 적용
            spinner2.setAdapter(adapterSpinner2);
        }else{
            // 스피너 ui 연결
            spinner2 = view.findViewById(R. id.spinner2);
        }




        return view;
    }


    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //한글, 영어, 숫자를 제외한 문자열 삭제
    public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, "");
        return str;
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------



}
