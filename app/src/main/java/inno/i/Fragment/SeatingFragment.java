package inno.i.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import inno.i.GlobalApplication;
import inno.i.R;

public class SeatingFragment extends Fragment {

    // 좌석의 상태
    static int seat_case;
    // 좌석의 번호
    static int seat_num;
    // 부모 액티비티 선언
    private Activity activity;
    // 모듈의 갯수
    static int modulelength;
    // 모듈의 상태를 받아올 스레드 핸들러
    MyHandler handler;
    // 핸들러의 시작/중지 플래그 값
    // 핸들러는 무한으로 돌기때문에 플래그 값을 명시했다(비 효율)
    static private boolean flag = true;

    int vehicle_num;

    // 아이의 nfc 값
    static String child_name;
    private Context context;

    // 좌석 프래그먼트 생성시 부모 액티비티에서 모듈의 갯수를 받아온다
    public static SeatingFragment newInstance(int module_length) {
        SeatingFragment fragment = new SeatingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        modulelength = module_length;
        return fragment;
    }

    // 프래그먼트가 액티비티와 연결 되었을때
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 상속 액티비티를 선언해 준다
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }


    // 버튼 선언
    Button btn[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context=container.getContext();

        GlobalApplication myApp = (GlobalApplication) activity.getApplication();
        vehicle_num = Integer.parseInt(GlobalApplication.getSelected_vehicle().get(3));

        Log.e( Integer.toString(vehicle_num), "vehicle_num");

        // 프래그먼트 xml 연결
        View view;
        if (vehicle_num == 34) {
            view=inflater.inflate(R.layout.seatting_page, container, false);
        }else if(vehicle_num ==11){
            view=inflater.inflate(R.layout.seatting_page, container, false);
        }else if(vehicle_num ==40){
            view=inflater.inflate(R.layout.seatting_page, container, false);
        }else{
            view=inflater.inflate(R.layout.seatting_page, container, false);
        }


        btn= new Button[vehicle_num];

        // 모듈 갯수에 맞게 버튼 생성
        for (int i = 0; i < vehicle_num; i++) {
            //button의 resource id 설정
            int resID = getResources().getIdentifier("btn_seat_"+(i+1), "id", activity.getPackageName());
            btn[i]=view.findViewById(resID);
        }


        // 핸들러 플래그를 시작으로vehicle_num
        start();

        // 핸들러 시작
        handler = new MyHandler();
        new Thread() {
            @Override
            public void run() {
                try {
                    // 플래그 값에 따라 시작/중지
                    while (flag) {
                        // 0.3초 간격 마다
                        sleep(300);
                        // 핸들러 함수 실행
                        handler.sendMessage(new Message());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트 중지시 핸들러 중지
        stop();
    }

    // 핸들러 시작/중지 함수
    public void stop() {
        flag = false;
    }
    public void start() { flag = true; }

    @SuppressLint("HandlerLeak")
    class MyHandler extends Handler {
        @Override
        // 핸들러 함수
        public void handleMessage(Message msg) {

            GlobalApplication myApp = (GlobalApplication) activity.getApplication();

            // 전역 변수에서 좌석의 번호와 좌석의 상태를 받아온다
            seat_num = GlobalApplication.getResnum()-1;//좌석번호는 1부터 시작하지만 좌석버튼은 0부터 시작하므로
            seat_case = GlobalApplication.getResid();
            child_name  = GlobalApplication.getChild_name();

        /*    Log.e("--------SeatingFragment","--------");
            Log.i(Integer.toString(seat_num)+"//"+Integer.toString(seat_case), "resnum//resid");
            Log.i(Integer.toString(myApp.getResid()), "myApp.getresid()");
*/

            // 그 좌석 상태에 맞게 이미지를 변경해준다
            if(seat_num>-1){
                if(seat_num>vehicle_num){ //없어두됨
                    Toast toast = Toast.makeText(context, vehicle_num+"인승 입니다. 버스를 재선택해주세요", Toast.LENGTH_SHORT);
                    toast.show();

                }else {
                    Log.e("1.seat_num",seat_num+"");
                    Log.e("2.child_name", child_name);
                    btn[seat_num].setText(child_name);
                    btn[seat_num].setBackgroundResource(seat_case);
                }

            }


            super.handleMessage(msg);
        }
    }

}
