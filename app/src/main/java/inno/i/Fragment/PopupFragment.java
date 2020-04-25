package inno.i.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.estimote.sdk.repackaged.okhttp_v2_2_0.com.squareup.okhttp.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import inno.i.GlobalApplication;
import inno.i.R;

public class PopupFragment extends DialogFragment {

    TextView pop_name;
    String mValue = "";
    // 상속 액티비티 선언
    private Activity activity;


    // 아이들 한 명의 정보를 임시로 담는 배열
    ArrayList<String> Student = new ArrayList<>();
    // 아이들의 세부 정보( 0:학생ID, 1:반ID, 2:이름, 3:성별, 4:생일, 5:부모님, 6:연락처, 7:주소, 8:반이름, 9:사진URL, 10:등원장소, 11:하원장소, 12:NFC데이터)
    ArrayList<String[]> Student_data = new ArrayList<>();
    // 경로와 목적지를 매칭한 노선 정보(현재 경로)
    ArrayList<String[]> Route_data = new ArrayList<>();
    // 목적지 당 학생 수
    ArrayList<String[]> Student_num= new ArrayList<>();

    private int flag = 0;
    private String reason="";
    //--------------------------------------------------------------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        View view = inflater.inflate(R.layout.attendpopup, container, false);


        Bundle mArgs = getArguments();

        if(mArgs != null) {
            mValue = mArgs.getString("key");
        }

        TextView tv1 = view.findViewById(R.id.tv1);
        TextView tv2 = view.findViewById(R.id.tv2);
        TextView tv3 = view.findViewById(R.id.tv3);


        final GlobalApplication myApp = (GlobalApplication) activity.getApplication();
        Route_data = GlobalApplication.getSelected_route();
        get_student();





        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalApplication.setLongclick_flag(1);
                Log.e(mValue+"어린이","도보");
                change_route();

                Bundle arguments = new Bundle();
                reason="도보";
                arguments.putString("reason",reason);

            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(mValue+"어린이","결석");
                change_route();
                Bundle arguments = new Bundle();
                reason="결석";
                arguments.putString("reason",reason);

            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(mValue+"어린이","차");
                change_route();
                Bundle arguments = new Bundle();
                reason="차";
                arguments.putString("reason",reason);

            }
        });

        return view;

    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------
    // 프래그먼트가 액티비티에 실행 될때 상속받은 액티비티를 정의해준다
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
        if (context instanceof Activity) {
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------
    public void onResume()
    {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(848, 928);
        window.setGravity(Gravity.CENTER);
        //TODO:
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void get_student(){
        String student_info;
        GlobalApplication myApp = (GlobalApplication) activity.getApplication();
        student_info = GlobalApplication.getStudent();
        Student.addAll(Arrays.asList(student_info.split("<br>")));
        for (int i = 0; i < Student.size(); i++) {
            Student_data.add(Student.get(i).split(","));
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void change_route(){

        Log.e("1","들어옴");
        GlobalApplication myApp = (GlobalApplication) activity.getApplication();
        int temp[] = new int[30];
        temp = GlobalApplication.getRoute_status();
        if(GlobalApplication.getSelected_type().equals("등원")) {//등원
            Log.e("2","들어옴");
            for(int i=0; i<Student.size(); i++) {
                 for(int j=0; j<Route_data.size(); j++) {
                     // 학생과 데이터의 학생이름과 같고
                     // 학생의 하차 or 승차 지점과 경로의 목적지와 같으면
                     Log.e("STUDENT1", Student_data.get(i)[2]);
                     Log.e("STUDENT2", Student_data.get(i)[10]);
                     Log.e("ROUTE", Route_data.get(j)[1]);
                     if (mValue.equals(Student_data.get(i)[2]) && Student_data.get(i)[10].equals(Route_data.get(j)[1])) {
                         Log.e("3", "들어옴");
                         temp[j]=temp[j]-1;
                         GlobalApplication.setRoute_status(temp);
                     }
                 }
            }
        }else{//하원
            Log.e("하원", "하원");
        }
        Log.i("경로가 변경되었습니다", "");
        for(int a=0; a<Route_data.size(); a++){
            Log.e("",Route_data.get(a)[1]);
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------

}
