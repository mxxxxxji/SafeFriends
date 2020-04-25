package inno.i.Fragment;

import android.content.Intent;
import android.os.Bundle;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import inno.i.Activity.QRCheckActivity;
import inno.i.Activity.ScanQRActivity;
import inno.i.R;

public class GetoffFragment extends Fragment {


    //운행종료버튼(drive_page4.xml)
    Button button;
    TextView textview;

    String str1 = "차량에";
    String str2 = "<font color = \"#FFABA4\"><u><strong> 남은 원생이 없는지 확인</strong></u></font>";
    String str3 = "해 주세요";
    public static GetoffFragment newInstance(){

        GetoffFragment fragment = new GetoffFragment();
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //프래그먼트 xml연결
        View view = inflater.inflate(R.layout.drive_page4, container, false);

        //운행종료버튼 연결
        button = view.findViewById(R.id.end_drive);

        //버튼에 텍스트 입력
        button.setText("운행종료");

        textview = view.findViewById(R.id.time_limit);

        textview.setText(Html.fromHtml(str1+str2+str3));



        //버튼 이벤트
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //액티비티 이동(운행종료->QR)
                Intent intent = new Intent(getActivity(), QRCheckActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//최상위 제외 액티비티 제거
                startActivity(intent);

            }
        });

        return view;
    }
}

