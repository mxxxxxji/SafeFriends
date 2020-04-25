package inno.i.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import inno.i.GlobalApplication;
import inno.i.R;

public class SetActivity extends AppCompatActivity {

    ImageButton btn_back;//뒤로가기 버튼
    ImageButton btn_module;//모듈 등록 버튼
    ImageButton btn_myinfo;//내정보 등록 버튼
    ImageButton btn_destination;//목적지 등록 버튼
    ImageButton btn_test;//테스트 주행 버튼
    ImageButton btn_logout;//로그아웃 버튼

    // 다이얼로그 빌더를 위한 액티비티 컨텍스트
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //ui세팅
        btn_back = findViewById(R.id.set_titlebar_back);
        btn_module = findViewById(R.id.set_module_btn);
        btn_myinfo = findViewById(R.id.set_myinfo_btn);
        btn_destination = findViewById(R.id.set_destination_btn);
        btn_test = findViewById(R.id.set_test_btn);
        btn_logout = findViewById(R.id.set_logout_btn);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btn_module.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ModuleActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btn_myinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyInfoActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btn_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DestinationActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btn_test.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // 다이얼로그 빌더 선언
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);

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
                Display display = getWindowManager().getDefaultDisplay();
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
                textView.setText("로그아웃 하시겠습니까?");

                // 확인 버튼 클릭 리스너
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        // 다이얼로그 제거
                        dig.dismiss();
                    }
                });
                // 취소 버튼 클릭 리스너
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
