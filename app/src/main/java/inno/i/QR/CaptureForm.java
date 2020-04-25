package inno.i.QR;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureForm extends CaptureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);


        /* TextVeiw를 설정하고 마지막엔 this.addContentView ! */
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setPadding(150, 500, 100, 100);
        textView.setText("QR 코드를 자동으로 인식합니다.");
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

        /* this.addContentView ! */
        this.addContentView(textView, params);

    }
}
