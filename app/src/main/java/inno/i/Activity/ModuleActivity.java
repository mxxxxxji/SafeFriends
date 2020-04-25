package inno.i.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import inno.i.Fragment.Seat34_Fragment;
import inno.i.GlobalApplication;
import inno.i.R;

public class ModuleActivity extends AppCompatActivity {

    ImageButton btn_back;//뒤로가기 버튼

    // 차량 정보가 담긴 배열 동적 할당
    ArrayList<String> Vehicle = new ArrayList<>();
    // 목적지의 세부 정보 (0 : 이름 1: 차번호 2:좌석수)
    ArrayList<String[]> Vehicle_data = new ArrayList<>();

    Spinner spinner_vehicle; //차량 스피너
    ArrayAdapter<String> arrayAdapter; //차량스피너 어뎁터
    ArrayList<String> spinner_str; //차량 스피너에 들어갈 문자열


    private boolean isFragment = true ;

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        //ui세팅
        spinner_vehicle = findViewById(R.id.module_spinner);
        btn_back = findViewById(R.id.set_titlebar_back);

        //뒤로가기 버튼 클릭 리스너
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        //차량정보를 불러온다
        get_vehicle();


        // 차량 스피너에 들어갈 문자열 리스트 선언
        spinner_str = new ArrayList<>();
        for (int i = 0; i < Vehicle.size(); i++) {
            spinner_str.add(Vehicle_data.get(i)[0]+"["+Vehicle_data.get(i)[1]+"]"+" - "+Vehicle_data.get(i)[2]+"인승");
        }

        // 차량스피너
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spinner_str);
        spinner_vehicle.setAdapter(arrayAdapter);
        spinner_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(getApplicationContext(),spinner_str.get(position)+"가 선택되었습니다.",
                        Toast.LENGTH_SHORT).show();
                switchFragment(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner_vehicle.setSelection(0);

    }//end of create

    //-------------------------------------------------------------------------------------------------------------------
    public void get_vehicle() {
        String vehicle_info;
        GlobalApplication myApp = (GlobalApplication) getApplication();
        vehicle_info = GlobalApplication.getVehicle();

        Vehicle.addAll(Arrays.asList(vehicle_info.split("<br>")));

        for (int i = 0; i < Vehicle.size(); i++){
            Vehicle_data.add(Vehicle.get(i).split(","));
        }

    }
    //-------------------------------------------------------------------------------------------------------------------
    public void switchFragment(int position){

        Fragment fragment;

        //현재 34인승만 구현되어있음, 수정필요
        if (position==0) {
            fragment = new Seat34_Fragment() ;
        } else if(position==1){
            fragment = new Seat34_Fragment() ;
        } else {
            fragment = new Seat34_Fragment() ;
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.replace(R.id.seat_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    //-------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
