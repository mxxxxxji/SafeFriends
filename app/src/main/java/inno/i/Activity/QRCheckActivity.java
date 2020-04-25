package inno.i.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.integration.android.IntentIntegrator;

import androidx.appcompat.app.AppCompatActivity;

import inno.i.GlobalApplication;
import inno.i.QR.CaptureForm;
import inno.i.R;

public class QRCheckActivity extends AppCompatActivity {

    Button qr_btn;//
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcheck);

        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        qr_btn = findViewById(R.id.qr_btn);
        imageView1 = findViewById(R.id.qr_check1);
        imageView2 = findViewById(R.id.qr_check2);
        imageView3 = findViewById(R.id.qr_check3);

        qr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //액티비티 이동(운행종료->QR)
                Intent intent = new Intent(QRCheckActivity.this, ScanQRActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//최상위 제외 액티비티 제거
                startActivity(intent);
            }
        });

        if(GlobalApplication.getQr_check()==1){
            imageView1.setImageResource(R.drawable.qr_check1);
        }else if(GlobalApplication.getQr_check()==2){
            imageView2.setImageResource(R.drawable.qr_check2);
        }else if(GlobalApplication.getQr_check()==3){
            imageView3.setImageResource(R.drawable.qr_check3);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
