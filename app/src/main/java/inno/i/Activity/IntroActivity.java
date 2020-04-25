package inno.i.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import inno.i.R;

public class IntroActivity extends AppCompatActivity {


    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {

            // 0.8초뒤에 다음화면(LoginActivity)으로 넘어가기 Handler 사용
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent); // 다음화면으로 넘어가기
            finish(); // Activity 화면 제거
            // 페이드인,아웃 효과 설정
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); // xml과 java소스를 연결

        //상태 바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 위치제공 퍼미션
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }

    } // end of onCreate

    @Override
    protected void onResume() {
        super.onResume();
        // 다시 화면에 들어왔을 때 예약 걸어주기
        handler.postDelayed(r, 800); // 0.8초 뒤에 Runnable 객체 수행
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 화면을 벗어나면, handler 에 예약해놓은 작업을 취소
        handler.removeCallbacks(r); // 예약 취소
    }
}
