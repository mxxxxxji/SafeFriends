package inno.i.SpinnerAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inno.i.GlobalApplication;
import inno.i.R;

public class SpinnerAdapter3 extends BaseAdapter {

    // 스피너 객체
    private Context context;
    // 스피너에 들어올 데이터
    private List<String> data;
    // 스피너 레이아웃을 연결할 인플레이터
    private LayoutInflater inflater;


    // 경로와 목적지를 매칭한 노선 정보
    ArrayList<String[]> Route_data = new ArrayList<>();
    // 아이들 정보를 담을 배열
    //ArrayList<String> Student = new ArrayList<>();
    //ArrayList<String[]> Student_data = new ArrayList<>();
    // 선택된 차량, 경로에 맞는 아이들의 세부정보( 0:학생ID, 1:반ID, 2:이름, 3:성별, 4:생일, 5:부모님, 6:연락처, 7:주소, 8:반이름, 9:사진URL, 10:등원장소, 11:하원장소, 12:NFC데이터)
    ArrayList<String[]> Selected_student = new ArrayList<>();

    // 아이들의 목적지가 담길 변수
    private static String area = "목적지";
    // 아이들의 목적지 까지의 상태를 담는 변수 (다음정류장에서 승차, 승차완료 등등)
    private static String status = "";
    // 이전, 다음 , 다다음 목적지 변수
    private String beforelocation;
    private String nextlocation;
    private String nextlocation2;
    // 텍스트뷰에 출력될 텍스트
    private static String text = "";
    // 경로 길이
    int path_num;



    // 어린이 출석체크 상태
    static int[] Children_arrive;
    // 텍스트뷰 선언
    TextView textView;
    // 임시 배열을 만들어 어린이 정보 문자열을 나눈다()
    String temp[] = new String[100];

    // 스피너 생성자
    public SpinnerAdapter3(Context context, final List<String> data) {

        // 생성됬을때 각 변수들 크기에 맞게 초기화
        this.context = context;
        this.data = data;//리스트에 들어갈 아이들 이름
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        GlobalApplication myApp = new GlobalApplication();
        Route_data = GlobalApplication.getSelected_route();
        Selected_student = GlobalApplication.getSelected_student();

        if(data.get(0).equals(null)){
            return;
        }else{

            Log.e("SpinnerAdpater3","-------------");

            temp = GlobalApplication.getChildren_status().split("");

            Log.e("1.temp",temp+"");
            // 어린이 출석체크 유무 배열 null 일시 초기화
            if (Children_arrive == null) {
                Children_arrive = new int[Selected_student.size()];
            }
            try{
                // 각 어린이에 맞게 임시 배열에 담긴 값을 어린이 도착배열에 담는다
                for (int i = 0; i < Children_arrive.length; i++) {
                    Children_arrive[i] = Integer.parseInt(temp[i + 1]);
                   // Log.e("2.AdptMain2: ", String.valueOf(Children_arrive[i]));
                }
            }catch(ArrayIndexOutOfBoundsException e){
                Log.e("학생 잘못 받아옴", "student_error");
            }

        }
    }

    // 스피너의 데이터 갯수를 반환하여 알수있다
    @Override
    public int getCount() {
        if (data != null) return data.size();
        else return 0;
    }

    // 스피너 헤더 설정
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GlobalApplication myApp = new GlobalApplication();
        nextlocation=myApp.getNextlocation();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner2, parent, false);
        }

        // 리스트 순서 에 맞게 어린이 리스트에서 값을 불러와 변수에 담는다
        text = data.get(position);
//        Log.e("1.position", position+"");

        // 해당 어린이의 목적지에 따른 상태 유무 변경
        area_chk();

        // null 오류 조건문
        if (data != null) {
            // 스피너 헤더에 있는 텍스트에 텍스트를 입력한다
            // 학생이름
            for(int i=0; i<Selected_student.size(); i++){
                Log.e("------------스피너쓰리ㅣㅣㅣㅣㅣㅣ","");
                Log.e("2.",Selected_student.get(i)[2]);
                Log.e("3.",Selected_student.get(i)[10]);
                Log.e("4.",text);
                Log.e("5.",nextlocation);
                if(Selected_student.get(i)[10].equals(nextlocation)){
                    if(Selected_student.get(i)[2].equals(text)){
                        ((TextView) convertView.findViewById(R.id.spinner_tv1)).setText(text);
                        ((TextView) convertView.findViewById(R.id.spinner_tv2)).setText(Selected_student.get(i)[10]);
                        ((TextView) convertView.findViewById(R.id.spinner_tv3)).setText(status);
                    }
                }

            }

        }else{
            Log.e("스피너쓰리", "널이었음");

        }
        return convertView;
    }

    // 스피너 그룹 멤버 설정
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner2_dropdown, parent, false);
            convertView.findViewById(R.id.layout).setBackgroundColor(context.getResources().getColor(R.color.spinner));
        }

        // 리스트 순서 에 맞게 어린이 리스트에서 값을 불러와 변수에 담는다
        text = data.get(position);


        // 해당 어린이의 목적지에 따른 상태 유무 변경
        area_chk();

        for(int i=0; i<Selected_student.size(); i++){
            if(Selected_student.get(i)[2].equals(text)){
                area=Selected_student.get(i)[10];//등원위치
            }
        }

        // 텍스트뷰 xml 연결 (이름|목적지|상태)
        ((TextView) convertView.findViewById(R.id.spinner_tv1)).setText(text);
        ((TextView) convertView.findViewById(R.id.spinner_tv2)).setText(area);
        ((TextView) convertView.findViewById(R.id.spinner_tv3)).setText(status);



        return convertView;
    }

    // 상속 함수들
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void
    area_chk() {
        // 상태를 확인하기 전 필요한 변수들을 전역변수에서서 불러다
        GlobalApplication myApp = new GlobalApplication();

        nextlocation = myApp.getNextlocation();
        nextlocation2 = myApp.getNextNextlocation();
        beforelocation = myApp.getBeforelocation();
        path_num = GlobalApplication.getRoute_check();

        try{
            // 목적지 하나마다
            for (int j = 0; j < Route_data.size(); j++) {
   //             Log.e("getCount", getCount()+"");
                // 스피너 멤버 전부를 검사한다(아이들)
                for (int i = 0; i < getCount(); i++) {
                    // 만약 스피너 멤버 중 한명과 어린이 정보를 불러왔을때의 어린이 이름과 일치한다면
                    // 스피너에는 아이들이 한명씩 있고 어린이 정보 배열에도 같은 아이들이 한명씩 있다
                    // 즉 스피너에서 이름을 통해 어린이 정보 배열에 접근하여 나머지 정보에 접근할 수 있다
                    if (text.equals(Selected_student.get(i)[2])) {
                        //그 어린이의 목적지는 어린이 정보에서 목적지를 불러오며
                        area = Selected_student.get(i)[10];
                        // (스피너를 열때마다 이 자바 클래스 전체가 실행된다)
                        // 스피너를 열었을때 해당 어린이가 출석체크가 완료된 상태라면
                        if (Children_arrive[i] == 1) {
                            status = "승차완료";
                            break;
                        }
                        // 목적지가 다음 목적지와 일치한다면
                        if (area.equals(nextlocation)) {
                            status = "승차예정";
                            // 목적지가 다다음 목적지와 일치한다면
                        } else if (area.equals(nextlocation2)) {
                            status = "다음정류장";
                            // 목적지에 이미 지나쳤다면
                        } else if (area.equals(beforelocation)) {

                            // 지나쳤는데 출석도 안되있으면
                            if (Children_arrive[i] == 0) {

                                status = "미출석";
                                break;
                            }
                            // 출석이 되있는데 지나쳤다면
                            status = "승차완료";

                            // 위에 해당이 안되면
                        } else {

                            // 경로 수 만큼 반복하여
                            for (int t = 0; t <= path_num; t++) {
                                // 목적지 정보와 비교하여 승차완료를 한번 더 검사
                                if (Route_data.get(t)[2].equals(area)) {

                                    status = "승차완료";
                                    break;
                                }
                                // 승차 완료가 안됬다면 00분후이다 (전,다음,다다음에도 해당이 안되는경우)
                                else {
                                    status = "약 00분후";
                                }
                            }
                        }
                    }
                }
            }
        }catch(IndexOutOfBoundsException e){
            Log.e("하나의 경로에", "같은 목적지가 두 개 있음");
        }
    }// end area_chk



}
