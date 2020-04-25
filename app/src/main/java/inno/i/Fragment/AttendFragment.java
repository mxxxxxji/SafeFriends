package inno.i.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import inno.i.Activity.GetoffActivity;
import inno.i.Activity.MainActivity;
import inno.i.GlobalApplication;
import inno.i.R;

import static android.widget.ListPopupWindow.WRAP_CONTENT;

public class AttendFragment extends Fragment {

    static int flag = 0;
    static int getoff = 0;


    // 상속 액티비티 선언
    private Activity activity;




    // 선택된 차량, 경로에 맞는 아이들의 세부정보( 0:학생ID, 1:반ID, 2:이름, 3:성별, 4:생일, 5:부모님, 6:연락처, 7:주소, 8:반이름, 9:사진URL, 10:등원장소, 11:하원장소, 12:NFC데이터)
    ArrayList<String[]> Selected_student = new ArrayList<>();

    // 경로와 목적지를 매칭한 노선 정보
    ArrayList<String[]> Route_data = new ArrayList<>();
    // 어린이 이미지를 담을 이미지 배열열
    ArrayList<Bitmap> children_pic = new ArrayList<>();
    // 출석체크 확인을 위한 배열
    ArrayList<Integer> attendance_check = new ArrayList<>();

    // 목적지마다 내릴 학생 수
    static int route_stauts[] = new int[30];
    // 어린이 출석체크 상태 배열
    static int[] Children_arrive;
    static String str;
    static String child_name;
    static String child_class;
    static String type;


    static String path_type;// 등/하원 체크
    static int start_check;
    static int route_check; // 경로체크

    static String reason;
    static int type_num;


    //--------------------------------------------------------------------------------------------------------------------------------------------------
    // 도착 액티비티에서 값을 받아온다
    public static AttendFragment newInstance() {
        AttendFragment fragment = new AttendFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // 도착 액티비티에서 값을 받아온다 받아온 값은 하차/출석 두가지를 나눈다
    public static AttendFragment newInstance2(int num) {
        AttendFragment fragment = new AttendFragment();
        flag = num;
        return fragment;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        GlobalApplication myApp = (GlobalApplication) activity.getApplication();

        path_type = GlobalApplication.getSelected_type();
        Log.e("path_type","<<"+path_type+">>");
        Route_data = GlobalApplication.getSelected_route();
        route_check = GlobalApplication.getRoute_check();
        attendance_check = GlobalApplication.getAttendance_check();
        route_stauts= GlobalApplication.getRoute_status();
        Selected_student = GlobalApplication.getSelected_student();


        if(path_type.equals("등원")){
            type_num=10;
        }else{
            type_num=11;
        }

        //경로에 맞는 학생들을 가져옴
        Selected_student = GlobalApplication.getSelected_student();
        Children_arrive = new int[Selected_student.size()];
        Log.e("1.getChildrend_status",""+ GlobalApplication.getChildren_status());
        // 아이들 상태가 널값이면 초기화를 시켜준다 (처음 실행했을때)
        if (GlobalApplication.getChildren_status() == null) {
            Log.e("처음실행","");
            String temp = "0";
            // 아이들 수만큼 0을 붙혀준다
            for (int i = 0; i < Selected_student.size() - 1; i++) {
                temp += "0";
            }

            // 그 뒤 전역변수에 저장
            GlobalApplication.setChildren_status(temp);
            Log.e("2.getChildrend_status",""+ GlobalApplication.getChildren_status());
        } else {
            Log.e("다음실행","");
            // 처음 실행이 아니라면
            // 전역변수에서 받아온 문자열을 임시 배열에 나누어 담아서
            String temp[];
            temp = GlobalApplication.getChildren_status().split("");
            Log.e("3.getChildrend_status", GlobalApplication.getChildren_status()+">>");
            Log.e("4.temp",""+temp[0]+temp[1]);
//            Log.e("5.length", Children_arrive.length+"");
            Log.e("6.length", temp.length+"");
            try{
                // 아이들 도착 배열에 저장해준다
                for (int i = 0; i < Children_arrive.length; i++) {
                    // i+1은 공백 처리
                    Children_arrive[i] = Integer.parseInt(temp[i + 1]);
                }
            }catch (ArrayIndexOutOfBoundsException e){

            }
        }
    }//end of onCrate

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

    // JELLY BEAN 이상
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // 프래그먼트 뷰 선언언
        View view = inflater.inflate(R.layout.attend_page, container, false);

        // 가장 바깥이 되는 레이아웃을 정해준다, 현재 5개만 구성하였다 (어린이 수에 따라 증축가능)
        LinearLayout linear1 = view.findViewById(R.id.linearLayout);
        LinearLayout linear2 = view.findViewById(R.id.linearLayout1);
        LinearLayout linear3 = view.findViewById(R.id.linearLayout2);
        LinearLayout linear4 = view.findViewById(R.id.linearLayout3);
        LinearLayout linear5 = view.findViewById(R.id.linearLayout4);


        // 어린이를 출석체크할 버튼을 선언한다
        final Button btn[] = new Button[Selected_student.size()];
        // 어린이 이름을 나타낼 텍스트를 선언한다
        final TextView tv[] = new TextView[Selected_student.size()];
        // 버튼과 텍스트를 합쳐줄 레이아웃 배열을 선언한다. (아이들 수에 맞춰짐)
        final LinearLayout linearLayout[] = new LinearLayout[Selected_student.size()];
        // 버튼이 없는 빈 부부을 채울 버튼,텍스트 및 레이아웃을 선언한다.
        final Button sub_btn[] = new Button[Selected_student.size()];
        final TextView sub_tv[] = new TextView[Selected_student.size()];
        final LinearLayout sub_Layout[] = new LinearLayout[Selected_student.size()];
        final TextView attend_tv;//도착장소 출력
        GlobalApplication myApp = (GlobalApplication) activity.getApplication();
        //도착장소 ui연결
        attend_tv = view.findViewById(R.id.attend_tv);
        attend_tv.setText(myApp.getNextlocation());



        // 아이들 수 만큼
        for (int i = 0; i < Selected_student.size(); i++){

            // 버튼 생성
            btn[i] = new Button(activity);
            // 버튼의 크기를 맞춰주고 (지금 고정적으로 생성했는데 화면 비율에 맞게 생성 해줘야 한다)
            btn[i].setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            // 버른의 id를 지정해준다
            btn[i].setId(i);
            // 빈 부분을 채울 버튼의 크기와 아이디를 정의한다
            sub_btn[i] = new Button(activity);
            sub_btn[i].setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            sub_btn[i].setId(i);
            int need_num = 4 - (Selected_student.size() % 4); // 번째
            int iNum;
            iNum = i / 4;  //줄

            // case 마다 1줄
            switch (iNum) {
                case 0:
                    // 선언한 레이아웃 배열에서 아이들 수에 맞게 레이아웃을 선언 한다
                    linearLayout[i] = new LinearLayout(activity);
                    linearLayout[i].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                    linearLayout[i].setOrientation(LinearLayout.VERTICAL);
                    linearLayout[i].setGravity(Gravity.CENTER);
                    linear1.addView(linearLayout[i]);

                    // 앞선 레이아웃에 맞게 텍스트 뷰를 선언한다
                    tv[i] = new TextView(activity);
                    tv[i].setText(Selected_student.get(i)[2]);
                    tv[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                    // 이전에 생성한 버튼과 방금 생성한 텍스트뷰를 레이아웃에 달아준다
                    linearLayout[i].addView(btn[i]);
                    linearLayout[i].addView(tv[i]);

                    Log.e("<<need_num>>",need_num+"");
                    // 이 라인에 빈 부분이 있다면
                    if (Selected_student.size() % 4 != 0 && i == Selected_student.size() - 1){

                    try{
                        // 빈 부분을 채울 수만큼
                        for (int j = 0; j < need_num; j++) {
                            // 레이아웃 생성
                            sub_Layout[j] = new LinearLayout(activity);
                            sub_Layout[j].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                            sub_Layout[j].setOrientation(LinearLayout.VERTICAL);
                            sub_Layout[j].setGravity(Gravity.CENTER);
                            linear1.addView(sub_Layout[j]);
                            // 텍스트뷰 생성
                            sub_tv[j] = new TextView(activity);
                            sub_tv[j].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));
                            // 버튼 및 텍스트뷰 추가
                            sub_Layout[j].addView(sub_btn[j]);
                            sub_btn[j].setBackgroundColor(Color.parseColor("#00ff0000")); // 투명
                            sub_Layout[j].addView(sub_tv[j]);
                        }
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        Log.e("ArrayIndexOutOfBounds", "Exception");
                    }

                    }
                    break;
                // 이후 동일하다
                case 1:
                    linearLayout[i] = new LinearLayout(activity);
                    linearLayout[i].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                    linearLayout[i].setOrientation(LinearLayout.VERTICAL);
                    linearLayout[i].setGravity(Gravity.CENTER);
                    linear2.addView(linearLayout[i]);

                    tv[i] = new TextView(activity);
                    tv[i].setText(Selected_student.get(i)[2]);
                    tv[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                    linearLayout[i].addView(btn[i]);
                    linearLayout[i].addView(tv[i]);

                    if (Selected_student.size() % 4 != 0 && i == Selected_student.size() - 1) {
                        for (int j = 0; j < need_num; j++) {

                            sub_Layout[j] = new LinearLayout(activity);
                            sub_Layout[j].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                            sub_Layout[j].setOrientation(LinearLayout.VERTICAL);
                            sub_Layout[j].setGravity(Gravity.CENTER);
                            linear2.addView(sub_Layout[j]);

                            sub_tv[j] = new TextView(activity);
                            sub_tv[j].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                            sub_Layout[j].addView(sub_btn[j]);
                            sub_btn[j].setBackgroundColor(Color.parseColor("#00ff0000")); // 투명
                            sub_Layout[j].addView(sub_tv[j]);
                        }
                    }

                    break;
                case 2:
                    linearLayout[i] = new LinearLayout(activity);
                    linearLayout[i].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                    linearLayout[i].setOrientation(LinearLayout.VERTICAL);
                    linearLayout[i].setGravity(Gravity.CENTER);
                    linear3.addView(linearLayout[i]);

                    tv[i] = new TextView(activity);
                    tv[i].setText(Selected_student.get(i)[2]);
                    tv[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                    linearLayout[i].addView(btn[i]);
                    linearLayout[i].addView(tv[i]);

                    if (Selected_student.size() % 4 != 0 && i == Selected_student.size() - 1) {
                        for (int j = 0; j < need_num; j++) {
                            sub_Layout[j] = new LinearLayout(activity);
                            sub_Layout[j].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                            sub_Layout[j].setOrientation(LinearLayout.VERTICAL);
                            sub_Layout[j].setGravity(Gravity.CENTER);
                            linear3.addView(sub_Layout[j]);

                            sub_tv[j] = new TextView(activity);
                            sub_tv[j].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                            sub_Layout[j].addView(sub_btn[j]);
                            sub_btn[j].setBackgroundColor(Color.parseColor("#00ff0000")); // 투명
                            sub_Layout[j].addView(sub_tv[j]);
                        }
                    }
                    break;
                case 3:
                    linearLayout[i] = new LinearLayout(activity);
                    linearLayout[i].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                    linearLayout[i].setOrientation(LinearLayout.VERTICAL);
                    linearLayout[i].setGravity(Gravity.CENTER);
                    linear2.addView(linearLayout[i]);

                    tv[i] = new TextView(activity);
                    tv[i].setText(Selected_student.get(i)[2]);
                    tv[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                    linearLayout[i].addView(btn[i]);
                    linearLayout[i].addView(tv[i]);

                    if (Selected_student.size() % 4 != 0 && i == Selected_student.size() - 1) {
                        for (int j = 0; j < need_num; j++) {
                            sub_Layout[j] = new LinearLayout(activity);
                            sub_Layout[j].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                            sub_Layout[j].setOrientation(LinearLayout.VERTICAL);
                            sub_Layout[j].setGravity(Gravity.CENTER);
                            linear4.addView(sub_Layout[j]);

                            sub_tv[j] = new TextView(activity);
                            sub_tv[j].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                            sub_Layout[j].addView(sub_btn[j]);
                            sub_btn[j].setBackgroundColor(Color.parseColor("#00ff0000")); // 투명
                            sub_Layout[j].addView(sub_tv[j]);
                        }
                    }
                    break;
                case 4:
                    linearLayout[i] = new LinearLayout(activity);
                    linearLayout[i].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                    linearLayout[i].setOrientation(LinearLayout.VERTICAL);
                    linearLayout[i].setGravity(Gravity.CENTER);
                    linear2.addView(linearLayout[i]);

                    tv[i] = new TextView(activity);
                    tv[i].setText(Selected_student.get(i)[2]);
                    tv[i].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                    linearLayout[i].addView(btn[i]);
                    linearLayout[i].addView(tv[i]);

                    if (Selected_student.size() % 4 != 0 && i == Selected_student.size() - 1) {
                        for (int j = 0; j < need_num; j++) {
                            sub_Layout[j] = new LinearLayout(activity);
                            sub_Layout[j].setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f));
                            sub_Layout[j].setOrientation(LinearLayout.VERTICAL);
                            sub_Layout[j].setGravity(Gravity.CENTER);
                            linear5.addView(sub_Layout[j]);

                            sub_tv[j] = new TextView(activity);
                            sub_tv[j].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, WRAP_CONTENT));

                            sub_Layout[j].addView(sub_btn[j]);
                            sub_btn[j].setBackgroundColor(Color.parseColor("#00ff0000")); // 투명
                            sub_Layout[j].addView(sub_tv[j]);
                        }
                    }
                    break;
                // 아이들 수에 맞게 증축 가능 현재 20명까지 가능하게 구성 (완전히 동적구현 가능할거같음)
                case 5:
                    break;

                default:
                    break;
            }

            btn[i].setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(final View v) {

                    for(int i=0; i<Children_arrive.length; i++)
                        Log.e("children_arrive",Children_arrive[i]+"");
                        Log.e("flag",flag+"");
                    // 최종 목적지에 도착했을경우엔 하차 이벤트
                    if (flag == 1 && path_type.equals("등원")) {
                        Log.e("최종목적지하차", "최종목적지하차");

                        // 다이얼로그 빌더 선언
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        LayoutInflater inflater = LayoutInflater.from(activity);

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
                        Display display = activity.getWindowManager().getDefaultDisplay();
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
                        textView.setText(Selected_student.get(v.getId())[2] + "어린이를 하차하겠습니까?");

                        // 확인 버튼 클릭 리스너
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                // 하차 했으므로 남아있는 인원을 1 줄이고
                                GlobalApplication myApp = (GlobalApplication) activity.getApplication();
                                int value = GlobalApplication.getRemainder_student();
                                value -= 1;
                                // 변경된 인원으로 저장
                                GlobalApplication.setRemainder_student(value);

                                // 하차한 어린이 버튼에 하차이미지를 합친다
                                btn[v.getId()].setBackgroundResource(R.drawable.check_after_icon);
                                // 어린이 이름 출력
                                btn[v.getId()].setText(Selected_student.get(v.getId())[2]);
                                btn[v.getId()].setTextColor(getResources().getColor(R.color.gray));

                                // 하차한 어린이 상태를 0으로 하여 빈자리로 만든다
                                Children_arrive[v.getId()] = 0;

                                // 변경된 어린이 상태 저장
                                arrayJoin();

                                // 텍스트뷰 변경
                                ((MainActivity) activity).changeText("출석예정 " + GlobalApplication.getTotal_child_num() + "명 | 출석현황 " + value + "명");

                                //------------------------------------------------------------------------------------------------------------------
/*
                                if(path_type.equals("등원")) {//등원-하차기록 db에 저장//등원-하차기록 db에 저장
                                    child_name = "name=\"" + Selected_student.get(v.getId())[2] + "\"&";
                                    child_class = "class=\"" + Selected_student.get(v.getId())[4] + "\"&";
                                    type = "type=arrive_1";
                                    str = "https://daerojung.cafe24.com/dbcon.php?";
                                    str = str + child_name + child_class + type;
//                                    putData(str);
                                }*/


                                //-----------------------------------------------------------------------------------------------------------------
                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });
                        //취소버튼 클릭 리스너
                        button_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });
                        // 하차 시작 플래그
                        getoff = 1;

                        // 하차는 아니며 빈좌석 일때 == 출석 체크
                    } else if (Children_arrive[v.getId()] == 0) {

                        // 다이얼로그 빌더 선언
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        LayoutInflater inflater = LayoutInflater.from(activity);

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
                        Display display = activity.getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);

                        // 비율에 맞게 다이얼로그 크기를 지정
                        Window window = dig.getWindow();

                        // 가로 화면의 80% 세로 40%
                        int x = (int) (size.x * 0.8f);
                        int y = (int) (size.y * 0.4f);

                        // 다이얼로그 크기 조정
                        window.setLayout(x, y);

                        // 다이얼로그 텍스트 변경
                        textView.setText(Selected_student.get(v.getId())[2] + " 어린이를 출석체크 하겠습니까?\n\n" +
                                path_type + " 장소: " + Selected_student.get(v.getId())[type_num] + "\n" +
                                " 현위치 : " + Route_data.get(route_check-1)[1]);



                        textView.setTextSize(12);

                        // 확인 버튼 클릭 리스너
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                GlobalApplication myApp = (GlobalApplication) activity.getApplication();

                                int value = GlobalApplication.getRemainder_student();
                                // 현재 인원을 1 증가 시켜주어
                                value += 1;
                                // 전역변수에 저장한다
                                GlobalApplication.setRemainder_student(value);

                                // 빈 이미지의 해당 어린이 버튼을 출석체크 상태의 이미지로 변경한다
                                if(Selected_student.get(v.getId())[3].equals("여성")){//여자아이일 경우
                                    btn[v.getId()].setBackgroundResource(R.drawable.girl);

                                }else{//남자아이일 경우
                                    btn[v.getId()].setBackgroundResource(R.drawable.boy);
                                }

                                // 어린이 이름 출력
                                btn[v.getId()].setText(Selected_student.get(v.getId())[2]);
                                btn[v.getId()].setTextColor(getResources().getColor(R.color.white));
                                // 해당 어린이를 착석상태로 변경한다
                                Children_arrive[v.getId()] = 1;



                                // 변경된 어린이 상태 저장
                                arrayJoin();



                                // 텍스트뷰 변경
                                if(path_type.equals("등원")){
                                   ((MainActivity) activity).changeText("출석예정 " + GlobalApplication.getTotal_child_num() + "명 | 출석현황 " + value + "명");
                                }else{
                                    ((MainActivity) activity).changeText("하차예정 " + GlobalApplication.getTotal_child_num() + "명 | 하차현황 " + value + "명");
                                }

                                //------------------------------------------------------------------------------------------------------------------
                              /*  if(path_type.equals(" 등원")){//등원-승차기록 db에 저장
                                    child_name= "name=\""+Selected_student.get(v.getId())[2]+"\"&";
                                    child_class= "class=\""+Children_data.get(v.getId())[8]+"\"&";
                                    type = "type=start_1";
                                    str = "https://daerojung.cafe24.com/dbcon.php?";
                                    str = str + child_name + child_class+type;
                                    putData(str);
                                }else{//하원-승차기록 db에 저장
                                    child_name= "name=\""+Children_data.get(v.getId())[2]+"\"&";
                                    child_class= "class=\""+Children_data.get(v.getId())[4]+"\"&";
                                    type = "type=start_2";
                                    str = "https://daerojung.cafe24.com/dbcon.php?";
                                    str = str + child_name + child_class+type;
                                    putData(str);
                                }*/
                                //-----------------------------------------------------------------------------------------------------------------

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

                        button_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });


                    } else if(path_type.equals("하원")&& GlobalApplication.getStart_check()==1){
                        // 다이얼로그 빌더 선언
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        LayoutInflater inflater = LayoutInflater.from(activity);

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
                        Display display = activity.getWindowManager().getDefaultDisplay();
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
                        textView.setText(Selected_student.get(v.getId())[2] + "어린이를 하차하겠습니까?");

                        // 확인 버튼 클릭 리스너
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                GlobalApplication myApp = (GlobalApplication) activity.getApplication();
                                int value = GlobalApplication.getRemainder_student();
                                value -= 1;
                                GlobalApplication.setRemainder_student(value);

                                // 다시 빈 좌석 이미지로 변경한다
                                btn[v.getId()].setBackgroundResource(R.drawable.check_after_icon);
                                // 어린이 이름 출력
                                btn[v.getId()].setText(Selected_student.get(v.getId())[2]);
                                btn[v.getId()].setTextColor(getResources().getColor(R.color.gray));
                                Children_arrive[v.getId()] = 0;
                                arrayJoin();

                                ((MainActivity) activity).changeText("하차예정 " + GlobalApplication.getTotal_child_num() + "명 | 하차현황 " + value + "명");
/*
                                if(path_type.equals(" 하원")) {//등원-하차기록 db에 저장//등원-하차기록 db에 저장
                                    child_name = "name=\"" + Selected_student.get(v.getId())[2] + "\"&";
                                    child_class = "class=\"" + Selected_student.get(v.getId())[4] + "\"&";
                                    type = "type=arrive_2";
                                    str = "https://innoi.co.kr/safe-school-og/test/app/dbcon.php?";
                                    str = str + child_name + child_class + type;
                                    //putData(str);
                                }*/
                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });

                        button_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });
                        // 하차 아님
                        getoff = 0;
                    }
                    else {// 하차도 아니며 출석도 아닐때 == 출석 취소
                        Log.e("출석취소", "출석취소");

                        // 다이얼로그 빌더 선언
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        LayoutInflater inflater = LayoutInflater.from(activity);

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
                        Display display = activity.getWindowManager().getDefaultDisplay();
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
                        textView.setText(Selected_student.get(v.getId())[2] + "어린이 출석체크를 취소하시겠습니까?");

                        // 확인 버튼 클릭 리스너
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                GlobalApplication myApp = (GlobalApplication) activity.getApplication();
                                int value = GlobalApplication.getRemainder_student();
                                value -= 1;
                                GlobalApplication.setRemainder_student(value);

                                // 다시 빈 좌석 이미지로 변경한다
                                btn[v.getId()].setBackgroundResource(R.drawable.check_after_icon);
                                // 어린이 이름 출력
                                btn[v.getId()].setText(Selected_student.get(v.getId())[2]);
                                btn[v.getId()].setTextColor(getResources().getColor(R.color.gray));
                                Children_arrive[v.getId()] = 0;
                                arrayJoin();
                                if(path_type.equals("등원")) {
                                    ((MainActivity) activity).changeText("출석예정 " + GlobalApplication.getTotal_child_num() + "명 | 출석현황 " + value + "명");
                                }else{
                                    ((MainActivity) activity).changeText("하차예정 " + GlobalApplication.getTotal_child_num() + "명 | 하차현황 " + value + "명");
                                }

                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });

                        button_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // 다이얼로그 제거
                                dig.dismiss();
                            }
                        });

                        // 하차 아님
                        getoff = 0;
                    }

                }

            });


            // 버튼의 롱클릭 이벤트
            btn[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View vl) {
                    // 다이얼로그 빌더 선언
                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    LayoutInflater inflater = LayoutInflater.from(activity);

                    // 다이얼로그 레이아웃 ui 선언
                    View view = inflater.inflate(R.layout.popup, null);

                    // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                    Button button1 = view.findViewById(R.id.btn1);
                    Button button2 = view.findViewById(R.id.btn2);
                    Button button3 = view.findViewById(R.id.btn3);

                    // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                    builder.setCancelable(false);

                    // 빌더 레이아웃 연결
                    builder.setView(view);

                    // 다이얼로그 생성
                    final AlertDialog dig = builder.create();

                    // 다이얼로그 출력
                    dig.show();

                    final GlobalApplication myApp = (GlobalApplication) activity.getApplication();
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(path_type.equals("등원")){
                                int value = GlobalApplication.getRemainder_student();
                                value -= 1;
                                GlobalApplication.setRemainder_student(value);
                            }else{
                                int value = GlobalApplication.getTotal_child_num();
                                value -= 1;
                                GlobalApplication.setTotal_child_num(value);
                            }

                            reason="도보";
                            GlobalApplication.setLongclick_flag(1);
                            btn[vl.getId()].setBackgroundResource(R.drawable.absent);
                            btn[vl.getId()].setText("도보");
                            btn[vl.getId()].setTextColor(getResources().getColor(R.color.white));

                            for(int i=0; i<Route_data.size(); i++){
                                Log.e("1.Selected_student",Selected_student.get(vl.getId())[type_num]);
                                Log.e("2.Route_data",Route_data.get(i)[1]);

                                if(Selected_student.get(vl.getId())[type_num].equals(Route_data.get(i)[1])){
                                    Log.e("2.Route_data",Route_data.get(i)[0]);
                                    route_stauts[Integer.parseInt(Route_data.get(i)[0])-1] -= 1;
                                    GlobalApplication.setRoute_status(route_stauts);
                                }
                            }
                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });

                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int value = GlobalApplication.getRemainder_student();
                            value -= 1;
                            GlobalApplication.setRemainder_student(value);
                            reason="결석";
                            GlobalApplication.setLongclick_flag(1);
                            btn[vl.getId()].setBackgroundResource(R.drawable.absent);
                            btn[vl.getId()].setText("결석");
                            btn[vl.getId()].setTextColor(getResources().getColor(R.color.white));
                            for(int i=0; i<Route_data.size(); i++){
                                Log.e("1.Selected_student",Selected_student.get(vl.getId())[type_num]);
                                Log.e("2.Route_data",Route_data.get(i)[1]);

                                if(Selected_student.get(vl.getId())[type_num].equals(Route_data.get(i)[1])){
                                    Log.e("2.Route_data",Route_data.get(i)[0]);
                                    route_stauts[Integer.parseInt(Route_data.get(i)[0])-1] -= 1;
                                    GlobalApplication.setRoute_status(route_stauts);
                                }
                            }
                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });

                    button3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int value = GlobalApplication.getRemainder_student();
                            value -= 1;
                            GlobalApplication.setRemainder_student(value);
                            reason="차";
                            GlobalApplication.setLongclick_flag(1);
                            btn[vl.getId()].setBackgroundResource(R.drawable.absent);
                            btn[vl.getId()].setText("차");
                            btn[vl.getId()].setTextColor(getResources().getColor(R.color.white));
                            for(int i=0; i<Route_data.size(); i++){
                                Log.e("1.Selected_student",Selected_student.get(vl.getId())[type_num]);
                                Log.e("2.Route_data",Route_data.get(i)[1]);

                                if(Selected_student.get(vl.getId())[type_num].equals(Route_data.get(i)[1])){
                                    Log.e("2.Route_data",Route_data.get(i)[0]);
                                    route_stauts[Integer.parseInt(Route_data.get(i)[0])-1] -= 1;
                                    GlobalApplication.setRoute_status(route_stauts);
                                }
                            }
                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });

                    // 해당 어린이를 결석으로 변경한다
                    Children_arrive[vl.getId()] = 2;

                    // 변경된 어린이 상태 저장
                    arrayJoin();

                    return false;
                }
            });
        }

    // 처음에 버튼에 아이들 이미지 입히는 부분
        // 아이들 수만큼
        for (int k = 0; k < Selected_student.size(); k++) {


                // 이미지를 불러오는데 빈자리라면 (출석체크 프래그먼트가 attach 될때마다 사진을 불러옴 (액티비티를 벗어난도 유지되는거 아님)
                if (Children_arrive[k] == 0) {
                    // 버튼을 빈자리 이미지로 불러온다

                    btn[k].setBackgroundResource(R.drawable.check_after_icon);
                    // 어린이 이름 출력
                    btn[k].setText(Selected_student.get(k)[2]);
                    btn[k].setTextColor(getResources().getColor(R.color.gray));

                    // 착석 되있다면 착석 이미지로 불러온다
                } else if(Children_arrive[k] == 1) {

                    // 빈 이미지의 해당 어린이 버튼을 출석체크 상태의 이미지로 변경한다
                    if(Selected_student.get(k)[3].equals("여성")){//여자아이일 경우
                        btn[k].setBackgroundResource(R.drawable.girl);

                    }else {//남자아이일 경우
                        btn[k].setBackgroundResource(R.drawable.boy);
                    }

                    // 어린이 이름 출력
                    btn[k].setText(Selected_student.get(k)[2]);
                    btn[k].setTextColor(getResources().getColor(R.color.white));
                }else{
                    btn[k].setBackgroundResource(R.drawable.absent);
                    // 어린이 이름 출력
                    btn[k].setText(reason);
                    btn[k].setTextColor(getResources().getColor(R.color.white));
                }

        }
        return view;
    }//end of onCreateView

    //--------------------------------------------------------------------------------------------------------------------------------------------------
    // 탑승한 어린이 들의 상태를 읽어와 액티비티를 전환할지를 검사한다
    public void arrayJoin() {
        String result = "";
        GlobalApplication myApp = (GlobalApplication) activity.getApplication();

        // 아이들의 출석상태 배열을 문자열로 변경한다
        for (int i = 0; i < Children_arrive.length; i++) {
            result += Children_arrive[i];
        }

        // else : 출석을 안한 어린이가 한명도 없다면
        if (result.contains("0")) {

        }
        else {

            //하원 출발 전 출석체크
            if(path_type.equals("하원")){
                GlobalApplication.setStart_check(1);
            }
            // 다이얼로그 빌더 선언
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = LayoutInflater.from(activity);

            // 다이얼로그 레이아웃 ui 선언
            View view = inflater.inflate(R.layout.popup_confirm, null);

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
            Display display = activity.getWindowManager().getDefaultDisplay();
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
            textView.setText("모든 인원이 탑승하였습니다.");

            // 확인 버튼 클릭 리스너
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 다이얼로그 제거
                    dig.dismiss();
                }
            });


        }

        // else : 모든 어린이가 탑승을 안했다면
        if (result.contains("1")) {

        } else {

            // 등원 하차 진행중이라면 or 하원시, 출발 출석체크완료했을 경우 모든 어린이가 탑승하지 않았다면
            if (getoff == 1) {
                // 다이얼로그 빌더 선언
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = LayoutInflater.from(activity);

                // 다이얼로그 레이아웃 ui 선언
                View view = inflater.inflate(R.layout.popup_confirm, null);

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
                Display display = activity.getWindowManager().getDefaultDisplay();
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
                textView.setText("모든 인원이 하차하였습니다.\n운행을 종료합니다.");

                // 확인 버튼 클릭 리스너
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 모든 인원 하차가 완료 되었으니 액티비티를 전환한다
                        Intent intent = new Intent(activity, GetoffActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                        startActivity(intent);
                        activity.finish();
                        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        // 다이얼로그 제거
                        dig.dismiss();
                    }
                });


            }
        }
        // 하차완료나 출석 완료가 아니면 생성한 문자열을 저장한다
        GlobalApplication.setChildren_status(result);

    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------

}
