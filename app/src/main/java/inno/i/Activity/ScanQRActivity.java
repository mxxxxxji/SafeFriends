package inno.i.Activity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.appcompat.app.AppCompatActivity;

import inno.i.GlobalApplication;
import inno.i.QR.CaptureForm;
import inno.i.R;

public class ScanQRActivity extends AppCompatActivity {

    private IntentIntegrator qrScan;
    // 상속 액티비티 선언
    private Activity activity;

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        qrScan = new IntentIntegrator(this);

        qrScan.setCaptureActivity(CaptureForm.class);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.initiateScan();


    }

    //-------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Intent intent;
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                intent = new Intent(getApplicationContext(), QRCheckActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                if(result.getContents().equals("a")){ //QR 코드 URL 들어가야함
                    GlobalApplication.setQr_check(1);
                    intent = new Intent(ScanQRActivity.this, QRCheckActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }



    //-------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
