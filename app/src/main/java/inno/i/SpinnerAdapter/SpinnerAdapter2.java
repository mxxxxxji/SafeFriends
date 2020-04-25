package inno.i.SpinnerAdapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import inno.i.GlobalApplication;
import inno.i.R;

public class SpinnerAdapter2 extends BaseAdapter {

    // 스피너의 객채
    private Context context;
    // 스피너에 들어올 리스트
    private ArrayList<String[]> data;
    // 스피너 레이아웃을 연결할 인플레이터
    private LayoutInflater inflater;
    // 다음 위치 변수 (스피너 헤드에 들어가야함)
    private String nextlocation;
    // 스피너 안 텍스트뷰 정의
    private TextView tv;


    //스피너 생성자
    public SpinnerAdapter2(Context context, ArrayList<String[]> data) {
        this.context = context;
        this.data = data;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data != null) return data.size();
        else return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GlobalApplication myApp = new GlobalApplication();
        nextlocation=myApp.getNextlocation();

        // 스피너 헤더의 xml을 연결해준다
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner1, parent, false);
        }
        if(data!=null){

            Log.e("------","SpinnerAdapter2-----");
            Log.e("1.nextlocation", nextlocation);
            // 데이터 세팅
            // 다음목적지 텍스트뷰 xml 연결
            ((TextView) convertView.findViewById(R.id.spinner_tv1)).setText("다음목적지");
            TextView textView = convertView.findViewById(R.id.spinner_tv2);

            // 텍스트 뷰 출력
            textView.setText(nextlocation);

            // 글자색 지정 (어플 기본색)
            textView.setTextColor(Color.parseColor("#3bb8f8"));
        }
        return convertView;
    }

    // 스피너 그룹 멤버 설정
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        GlobalApplication myApp = new GlobalApplication();
        nextlocation=myApp.getNextlocation();

        Log.e("1.nextlocation2", nextlocation);
        // 스피너 멤버에 xml을 연결해준다
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner1_dropdown, parent, false);

            // 멤버 텍스트뷰 연결
            tv = convertView.findViewById(R.id.spinner_tv2);
        }

        // 리스트에 들어있는 값을 가져와 (순서대로 리스트에 넣었기 때문에 postion 으로 가져오면 된다)
        String text = data.get(position)[1];

        // 출력 해준다
        tv.setText(text);

        // 다음 목적지는 빨간색으로
        if (text.equals(nextlocation)) {
            tv.setTextColor(Color.RED);
        } else {
            tv.setTextColor(Color.LTGRAY);
        }
        return convertView;
    }


    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
