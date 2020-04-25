package inno.i.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import androidx.fragment.app.Fragment;
import inno.i.GlobalApplication;
import inno.i.R;

public class Seat34_Fragment extends Fragment {


    // 부모 액티비티 선언
    private Activity activity;


    String seatnum;


    public Seat34_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //xml 레이아웃이 인플레이트 되고 자바소스 코드와 연결이된다.
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.seat34, container, false);

        Button btn[]=new Button[34];

        GlobalApplication myApp = (GlobalApplication) activity.getApplication();

        for (int i = 0; i <34 ; i++){
            String str1 = "https://innoi.co.kr/safe-school-og/test/app/module_insert.php?";
            str1 += "seatnum=";
            str1 += Integer.toString(i+1);//좌석번호
            str1 += "&school_name=";
            str1 += myApp.getSchool_name();//유치원명
            str1 += "&vehicle=";
            str1 += myApp.getSelected_vehicle().get(1);//차량명*/
            Log.e("str1",str1);
            putData(str1);
        }


        for(int i=0; i<34; i++){
            final int seat_num=i+1;

            //button의 resource id 설정
            int resID = getResources().getIdentifier("btn_seat_"+(i+1), "id", activity.getPackageName());
            btn[i]=rootView.findViewById(resID);

            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 다이얼로그 빌더 선언
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = LayoutInflater.from(getActivity());

                    // 다이얼로그 레이아웃 ui 선언
                    View view = inflater.inflate(R.layout.popup_module, null);

                    // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                    Button button = view.findViewById(R.id.btn_confirm);
                    Button button_cancel = view.findViewById(R.id.btn_cancel);
                    TextView textView = view.findViewById(R.id.tv_content2);
                    final EditText editText = view.findViewById(R.id.et_content);

                    // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                    builder.setCancelable(false);

                    // 빌더 레이아웃 연결
                    builder.setView(view);

                    // 다이얼로그 생성
                    final AlertDialog dig = builder.create();

                    // 다이얼로그 출력
                    dig.show();

                    // 다이얼로그 크기를 위해 현재 화면 비율을 가져온다.
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);

                    // 비율에 맞게 다이얼로그 크기를 지정
                    Window window = dig.getWindow();

                    // 가로 화면의 80% 세로 30%
                    int x = (int) (size.x * 0.9f);
                    int y = (int) (size.y * 0.6f);

                    // 다이얼로그 크기 조정
                    window.setLayout(x, y);

                    // 다이얼로그 텍스트 변경
                    textView.setText("좌석번호-"+Integer.toString(seat_num)+"번");

                    // 확인 버튼 클릭 리스너
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String userid = editText.getText().toString();
                            Log.v("모듈번호", userid);
                            String str = "https://innoi.co.kr/safe-school-og/test/app/dbcon_module_update.php?";
                            str +="userid=";
                            str += userid;
                            str +="&seatnum=";
                            str +=Integer.toString(seat_num);
                            str +="&school_name=";
                            str +=GlobalApplication.getSchool_name();
                            str +="&vehicle=";
                            str +=GlobalApplication.getSelected_vehicle().get(1);

                            Log.e("str",str);
                            putData(str);

                            String msg = Integer.toString(seat_num)+"번 좌석"+editText.getText().toString()+" 모듈등록 완료되었습니다";
                            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

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
                }
            });
        }


        return rootView;

    }

    // 프래그먼트가 액티비티에 실행 될때 상속받은 액티비티를 정의해준다
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }

    //DB에 데이터 넣는 함수
    public void putData(String url){
        class PutDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                String uri = strings[0];
                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();


                }catch (Exception e){
                    return null;
                }

            }
        }
        PutDataJSON g = new PutDataJSON();
        g.execute(url);
    }
}
