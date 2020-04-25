package inno.i.SpinnerAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.cloud.internal.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inno.i.GlobalApplication;
import inno.i.R;

public class SpinnerAdpater1 extends BaseAdapter { //SelectedActivity의 선생님, 차량 스피너에 사용됨

    Context context;
    ArrayList<String[]> data;
    LayoutInflater inflater;
    private String type;

    public SpinnerAdpater1(Context context, ArrayList<String[]> data, String str){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        type =str;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.spinner_spinner1_normal, parent, false);
        }


        ((TextView) convertView.findViewById(R.id.spinnerText_nomal)).setText("차량선택");
        ((TextView) convertView.findViewById(R.id.spinnerText_nomal)).setText("선생님선택");

        if(data!=null){
            Log.e("data는 ",data.get(position)[1]);
            try{
                //받은 문자열에 따라 text변경
                TextView textView = convertView.findViewById(R.id.spinnerText_nomal);
                if(type.equals("vehicle")) {
                    ((ImageView) convertView.findViewById(R.id.spinner_image)).setImageResource(R.drawable.vehicle);
//                ((TextView) convertView.findViewById(R.id.spinnerText_nomal)).setHint("차량명");
                    textView.setText(data.get(position)[1]);//차량명
//                    textView.setText("차량명");//차량명
                }else if(type.equals("teacher")){

                    ((ImageView) convertView.findViewById(R.id.spinner_image)).setImageResource(R.drawable.teacher);
                    textView.setText(data.get(position)[1]);//이름

                }
            }
            catch (ArrayIndexOutOfBoundsException e){
                Log.e("해당 유치원에 등록된", "차량이 없음");
            }

        }
        return convertView;
    }


    @Override
    public int getCount() {
        if(data!=null) return data.size();
        else return 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.spinner_spinner1_dropdown, parent, false);
        }

        //데이터세팅
        if(type.equals("vehicle")) {
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(data.get(position)[1]);//차량명
            ((TextView)convertView.findViewById(R.id.spinnerText2)).setText(data.get(position)[2]);//차량번호
        }
        else if(type.equals("teacher")){
            ((TextView)convertView.findViewById(R.id.spinnerText)).setText(data.get(position)[1]);//이름
            ((TextView)convertView.findViewById(R.id.spinnerText2)).setText(data.get(position)[6]);//반
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
