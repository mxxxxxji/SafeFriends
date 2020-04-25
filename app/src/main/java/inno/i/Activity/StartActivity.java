package inno.i.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import inno.i.GlobalApplication;
import inno.i.R;

import static java.lang.Integer.parseInt;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class StartActivity extends AppCompatActivity {
    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 위도 경도를 담을 변수 static 이 코드 전체에 부담을 준다.
    // 가능 하면 지역 변수로 그리고 클래스 마다 간섭하지 못하게 하는것이 좋다.
    static double latitude;
    static double longitude;

    // 속도를 담을 변수
    static double speed;

    // 위치 제공자가 누군지를 담을 변수 (실내면 네트워크 실외면 gps) 이 값은 현재 테스트를 위해 운행 기록 데이터에 올라간다.
    static String status;

    // 데이터 베이스 운행기록 datetime 속성에 들어갈 시간값 형식
    static long mNow;
    static Date mDate;
    static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    // 아이들 한 명의 정보를 임시로 담는 배열
    ArrayList<String> Student = new ArrayList<>();
    // 아이들의 세부 정보( 0:학생ID, 1:반ID, 2:이름, 3:성별, 4:생일, 5:부모님, 6:연락처, 7:주소, 8:반이름, 9:사진URL, 10:등원장소, 11:하원장소, 12:NFC데이터)-전체학생임
    ArrayList<String[]> Student_data = new ArrayList<>();
    // 선택된 차량, 경로에 맞는 아이들의 세부정보( 0:학생ID, 1:반ID, 2:이름, 3:성별, 4:생일, 5:부모님, 6:연락처, 7:주소, 8:반이름, 9:사진URL, 10:등원장소, 11:하원장소, 12:NFC데이터)
    ArrayList<String[]> Selected_student = new ArrayList<>();

    // 목적지 하나의 정보를 임시로 담는 배열
    ArrayList<String> Destination = new ArrayList<>();
    // 목적지의 세부 정보 (0 :목적지ID 1:목적지 이름 2:위도 3: 경도)
    ArrayList<String[]> Destination_data = new ArrayList<>();
    // 경로 하나의 정보를 임시로 담는 배열
    ArrayList<String> Course = new ArrayList<>();
    // 경로의 세부 정보 (0 :경로이름 1:경로 2:등/하원 )
    ArrayList<String[]> Course_data = new ArrayList<>();
    // 모듈 하나의 정보를 임시로 담는 배열
    ArrayList<String> Module = new ArrayList<>();
    // 각 모듈정보를 담을 배열 선언 (0 :유저 id, 1:좌석번호, 2:유치원, 4:차량 이름)
    ArrayList<String[]> Module_data = new ArrayList<>();

    // 경로와 목적지를 매칭한 노선 정보
    ArrayList<String[]> Route_data = new ArrayList<>();

    // 경로 배열에서 다음위치 인덱스
    static int route_check;

    // 승/하 차한 어린이들
    // 어린이가 6명이라면 000000
    // 두번째 어린이가 승/하 했을경우 010000
    static int[] Children_arrive;


    // 운행기록, 경고음 스레드 선언
    Thread thread = new Thread(new playBeep());
    // 스레드에 사용될 핸들러
    private Handler handler = new Handler();
    // 블루투스 비콘 스캐너 및 핸들러 선언
    private BluetoothLeScanner mBLEScanner;
    private Handler scanHandler = new Handler();
    // 경고음
    MediaPlayer mediaPlayer;
    // 비프음 핸들러의 flag 역활 (true : 중지 / false : 실행)
    static boolean stopped = true;

    // 해당 지역에 도착 했을때 다이얼로그가 팝업 되어서 다이얼로그가 중첩된다. 그걸 방지 하기 위한 플래그 값
    // 0이면 팝업 되며 1이면 팝업 되지 않는다.
    static int dialog_flag = 0;
    // 다이얼로그 빌더를 위한 액티비티 컨텍스트
    final Context context = this;
    static int type_num1;
    static int type_num2;
    static int type_num3;
    // 정보 확인
    static int info_check = 0;

    static LocationManager lm;
    //--------------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        //Activity가 실행되는 동안 화면이 꺼지지 않도록 한다
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        GlobalApplication myApp = (GlobalApplication) getApplication();
        // 아이들 정보 받아오는 함수



        //  ui
        TextView name_textView;
        TextView class_textView;
        TextView vehicle_textView;
        TextView vehicle_num_textView;
        ImageButton drivestart_Imagebutton;
        ImageButton btn_back;
        ImageButton btn_setting;//설정버튼

        name_textView = findViewById(R.id.StartActivity_textView_name);
        class_textView = findViewById(R.id.StartActivity_textView_class);
        vehicle_textView = findViewById(R.id.StartActivity_textView_vehicle);
        vehicle_num_textView = findViewById(R.id.StartActivity_textView_vehicle_num);


        drivestart_Imagebutton = findViewById(R.id.StartActivity_imageButton_start);
        btn_back = findViewById(R.id.titlebar_back);
        btn_setting =findViewById(R.id.titlebar_setting);

        // 블루투스 비콘 초기화
        BluetoothManager btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        mBLEScanner = btAdapter.getBluetoothLeScanner();

        // 권한 체크
        checkPermission();

        //운행 전 : 0, 운행시작:1
        GlobalApplication.setMoving_check(0);
        // 현재 경로 설정
        GlobalApplication.setRoute_check(0);


        // selectactivity 에서 받아온 등/하 원 선택
        String type = myApp.getSelected_type();
        if(type.equals("등원")){
            type_num1=13;
            type_num2=7;
            type_num3=10;
        }else{
            type_num1=14;
            type_num2=8;
            type_num3=11;
        }
        Toast.makeText(getApplicationContext(), type + " 입니다.", Toast.LENGTH_SHORT).show();

        get_student();
        children_status();

        get_course();
        get_destination();
        get_module();



        //차량의 경로 정보를 저장할 동적배열
        ArrayList<String[]> temp1 =new ArrayList<>();
        //차량의 경로를 목적지 정보와 매칭
        for(int i=0; i<Course_data.size(); i++){
            ArrayList<String> temp2 = new ArrayList<>();
            if(GlobalApplication.getSelected_type().equals("등원")){
                Log.e("등원", "경로");
                if(GlobalApplication.getSelected_vehicle().get(7).equals(Course_data.get(i)[0]))//차량의 경로이름과 DB의 경로이름이 같다면
                    temp2.addAll(Arrays.asList(Course_data.get(i)[1].split("/")));//경로를 "/"기준으로 잘라서 임시배열에 넣음
            }else{//
                Log.e("하원", "경로");
                if(GlobalApplication.getSelected_vehicle().get(8).equals(Course_data.get(i)[0]))//차량의 경로이름과 DB의 경로이름이 같다면
                    temp2.addAll(Arrays.asList(Course_data.get(i)[1].split("/")));//경로를 "/"기준으로 잘라서 임시배열에 넣음
            }
            for(int j=0; j<temp2.size(); j++){
                for(int k=0; k<Destination_data.size(); k++){
                    //목적지 id와 경로 id가 같으면 해당 차량의 하차 목적지 저장
                    if(StringReplace(Destination_data.get(k)[0]).equals(temp2.get(j))){
                        temp1.add(Destination_data.get(k));
                    }
                }
            }
        }
        //전역변수에 저장
        GlobalApplication.setSelected_route(temp1);
        Route_data = GlobalApplication.getSelected_route();
        route_check = GlobalApplication.getRoute_check();
        Student_num();

        // 현 위치를 저장하는 위치 리스너
        // LocationManager 객체를 얻어온다
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // GPS 위치 제공자
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                500, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        // Network 위치 제공자
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                500, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);






        // 화면에 텍스트 출력
        vehicle_textView.setText(GlobalApplication.getSelected_vehicle().get(1));                   //차량명
        vehicle_num_textView.setText("("+ GlobalApplication.getSelected_vehicle().get(2)+")");    //차량번호
        name_textView.setText(GlobalApplication.getSelected_teacher().get(1));                      //선생님이름
        class_textView.setText("("+ GlobalApplication.getSelected_teacher().get(6) + ") | ");          //선생님반


        //뒤로가기버튼 클릭리스너
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalApplication myApp = (GlobalApplication) getApplication();
                GlobalApplication.setChildren_status(null);
                // 뒤로가기 버튼 클릭시 위치리스너 종료
                lm.removeUpdates(mLocationListener);
                // 전역변수 초기화 후 운행종료
                myApp.init_before_start();
                Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        //설정버튼 클릭리스너
        btn_setting.setOnClickListener(new View.OnClickListener() {
            // 취소 버튼 이벤트
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                startActivity(intent);
                // 페이드인,아웃 효과 설정
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        // 시작 버튼 리스너
        drivestart_Imagebutton.setOnClickListener(new View.OnClickListener() {
            // 운행시작 버튼 이벤트
            @Override
            public void onClick(View v) {
                GlobalApplication myApp = (GlobalApplication) getApplication();
                int flag = 0;

                for (int i = 0; i < Student.size(); i++) {
                    if(Student_data.get(i)[type_num1].equals(myApp.getSelected_vehicle().get(type_num2))){
                        flag += 1;
                    }
                }
                if(flag == 0 ){
                    Log.e("null", "null null");
                  //  Log.e("학생"+i+"의 등원경로",Student_data.get(i)[type_num1]);
                  //  Log.e("차량의 등원경로",myApp.getSelected_vehicle().get(type_num2));
                    // 다이얼로그 빌더 선언
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);

                    // 다이얼로그 레이아웃 ui 선언
                    View view = inflater.inflate(R.layout.popup_confirm, null);

                    // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                    Button button = view.findViewById(R.id.btn_confirm);
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
                    textView.setText("해당 경로에 등록된 어린이가 없습니다. 어린이를 등록해주세요");

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 다시 다이얼로그 플래그를 0으로 한다.
                            dialog_flag = 0;

                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });
                }else if(info_check==0){
                    GlobalApplication.setMoving_check(1);
                    GlobalApplication.setRoute_check(GlobalApplication.getRoute_check()+1);
                    // activity 이동
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                    // 스레드 시작
                    onThreadStart();
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = LayoutInflater.from(context);

                    // 다이얼로그 레이아웃 ui 선언
                    View v2 = inflater.inflate(R.layout.popup_confirm, null);

                    // 다이얼로그 버튼 및 텍스트뷰 ui 연결
                    Button button = v2.findViewById(R.id.btn_confirm);
                    TextView textView = v2.findViewById(R.id.tv_content);

                    // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
                    builder.setCancelable(false);

                    // 빌더 레이아웃 연결
                    builder.setView(v2);

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
                    textView.setText("운전자, 등원경로, 하원경로가 배정되지 않았습니다");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 다시 다이얼로그 플래그를 0으로 한다.
                            dialog_flag = 0;

                            // 다이얼로그 제거
                            dig.dismiss();
                        }
                    });

                }

            }
        });




    }//end of oncreate

    //--------------------------------------------------------------------------------------------------------------------------------------------
    public void onThreadStart(){
        // 경고음, 운행기록 스레드 시작
        thread.start();
        scanHandler.post(scanRunnable); // 운행시작 버튼 클릭시 비콘값 받아옴
        GlobalApplication myApp = (GlobalApplication) getApplication();
        // 전역변수에 스레드 플래그 값을을 tre 시켜 현재 스레드거  ON 이라는것을 알려준다
        GlobalApplication.setThread(true);
    }


    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 블루투스 스캐너 시작 (운행시작 ~ 어플종료)
    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            mBLEScanner.startScan(null, getScanSettings(), leScanCallback);
        }
    };

    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 비콘 스캐너 설정
    private static ScanSettings getScanSettings() {
        final ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setReportDelay(0);
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        return builder.build();
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 비콘 스캔 지정 범위 및 16진수 읽어 오기
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            int startByte = 2;
            boolean patternFound = false;
            byte[] scanRecord = result.getScanRecord().getBytes();

            while (startByte <= 5) {
                if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && ((int) scanRecord[startByte + 3] & 0xff) == 0x15) {
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound) {
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                //UUID detection : 비콘의 데이터 값 (0번째에서 15번째 까지)
                String device_id = hexString.substring(0, 16);
                String nfc_data = hexString.substring(20, 28);
                //Log.i(nfc_data, "nfc_data");


                // clip value : 안전벨트의 상태 (비콘 값 16에서 18까지)
                int clipValue;
                try {
                    clipValue = parseInt(hexString.substring(16, 18));

                } catch (Exception e) {
                    clipValue = 0;
                }

                // major : 비콘 값의 major부분분
                byte[] userBytes = new byte[4];
                userBytes[0] = scanRecord[startByte + 20];
                userBytes[1] = scanRecord[startByte + 21];
                userBytes[2] = scanRecord[startByte + 22];
                userBytes[3] = scanRecord[startByte + 23];

                String user_id = bytesToHex(userBytes);

                //----------------------------------------------------------------------------------------------------------------------------
                for (int i = 0; i < Module.size(); i++) {


                    Log.e("--------StartActivity","--------");

                    Log.e("1.device_id",device_id);
                    Log.e( "2.Module_data.get(i)[0]",Module_data.get(i)[0]);
                    Log.e("3.user_id",user_id);
                    Log.e("4.Module_data.get(i)[1]",Module_data.get(i)[1]);
                    Log.e("equls확인",Module_data.get(i)[0].equals(user_id)+"");
                    // DB의 모듈(device_id, user_id)과 일치하면
                    if ("496E6E6953616665".equals(device_id) && Module_data.get(i)[0].equals(user_id)){

                        // 비프음 정지 (비프음은 항상 대기하고 있으며 플레그 값에 따라 작동된다 (비효율적))
                        stopped = true;
                        for(int j=0; j<Student.size(); j++){
                            GlobalApplication myApp = (GlobalApplication) getApplication();

 //                           String flag = String.valueOf(myApp.getChildren_status().charAt(j));
                            //Log.e(flag,"flag"+j);
                        }

                        for(int j=0; j<Student.size(); j++){
                            // 비콘에 담긴 NFC 데이터와 어린이의 NFC 데이터가 일치하면(=출석체크가 되었다면)
                            // 현재 출석체크는 직접 누르는 걸로 구현 되어있으므로 출석체크 후 seatFragment로 넘어가주어야 한다
                            Log.i("22222", "22222");
                            Log.i(Student_data.get(j)[12]+"//j:"+j,"Children_data.get(j)[12]");
                            Log.i(nfc_data,"nfc_data");
                            Log.i("equls확인",(Student_data.get(j)[12]).equals(nfc_data)+"");
                            if((Student_data.get(j)[12]).equals(nfc_data)){



                                try {
                                    //아래 코드 대신 여기 NFC데이터가 같으면 출석체크되는 코드가 구현되어야 함
                                    String flag = String.valueOf(GlobalApplication.getChildren_status().charAt(j));
                                    Log.i(flag,">flag");
                                    if(GlobalApplication.getChildren_status() == null){
                                        return;
                                    }

                                    // 좌석에 어린이가 출석체크 하여 앉아 있다면
                                    if (flag.equals("1")) {

                                        if (clipValue == 1){ // 안전 벨트 착용 시
                                            Log.e("3333", "3333");
                                            //  앉은 좌석 이미지를 seatfragment로 보내준다. (seatfragment 에서 이 값을 받아 받아온 이미지로 좌석 변경 )
                                            GlobalApplication.setResid(R.drawable.belt1);
                                            GlobalApplication.setResnum(Integer.parseInt(Module_data.get(i)[1]));//좌석번호 전송
                                            GlobalApplication.setChild_name(Student_data.get(j)[2]);//아이의 이름 전송, 몇 번 좌석에 누가 앉았는지 출력하기 위하여

                                            Log.e(Integer.toString(GlobalApplication.getResnum())+"//"+Integer.toString(GlobalApplication.getResid())+"333333", "resnum//resid");
//                                        myApp.setWarning(0);//경고:1, else 0
                                        }
                                        else{ // 안전벨트 해제 시 or 미착용 시
                                            Log.e("44444", "44444");
                                            // 비프음 실행
                                            stopped = false;
                                            // 좌석을 벨트가 풀린 이미지로 바꾸어준다
                                            GlobalApplication.setResid(R.drawable.belt2);
                                            GlobalApplication.setResnum(Integer.parseInt(Module_data.get(i)[1]));//좌석번호 전송
                                            GlobalApplication.setChild_name(Student_data.get(j)[2]);//아이의 이름 전송

                                            Log.e(Integer.toString(GlobalApplication.getResnum())+"//"+Integer.toString(GlobalApplication.getResid())+"4444", "resnum//resid");

//                                        vehicle_status=myApp.getVehicle_status();
//                                        Log.i("운행상황"+vehicle_status, "StartActivity");

/*                                        if(vehicle_status==1){
                                            myApp.setWarning(1);
                                        }
                                        Log.i("warning:"+myApp.getWarning(),"StartActivity");
*/
                                        }
                                    } else{
                                        Log.e("55555", "55555");
                                        // 빈 좌석 이미지를 seatfragment로 보내준다.
                                        GlobalApplication.setResid(R.drawable.belt3);
                                        GlobalApplication.setChild_name(Module_data.get(i)[1]);//아이의 이름 대신 좌석번호 전송(빈좌석이므로 번호가 들어가야함)
                                        GlobalApplication.setResnum(Integer.parseInt(Module_data.get(i)[1]));//좌석번호 전송
//                                    Log.e(Integer.toString(myApp.getResnum())+"//"+Integer.toString(myApp.getResid())+"5555", "resnum//resid");
                                    }

                                }catch (NullPointerException e){
                                    Log.i("null", "null");
                                }
                            }
                        }

                    }
                    else{
                        //자회사의 비콘이 아니므로 다른 처리 해주어야함
//                        Log.e("77777", "77777");
                    }
                }
                //----------------------------------------------------------------------------------------------------------------------------
            }

            GlobalApplication myApp = (GlobalApplication) getApplication();
            if(!GlobalApplication.getThread()){
                mBLEScanner.stopScan(leScanCallback);
            }
            processResult(result);
        }

        // 스캔의 결과값
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
        }

        private void processResult(final ScanResult result) {
        }
    };

    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 16진수로 받아오기 위해 배열 정의
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    // 비콘의 바이트 값을 위의 배열에 맞게 변환 (코드를 인용한 것이라 정확하게 설명 불가 Created by jonghwa on 2018-01-10.)
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        //Log.i(new String(hexChars, 0, hexChars.length),"hexChars");
        return new String(hexChars);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 비프음 발생 스레드
    private class playBeep implements Runnable {
        // 스레드 실행
        @Override
        public void run() {
            while (true) {
                GlobalApplication myApp = (GlobalApplication) getApplication();
                // 스레드 중지 신호를 받으면 이 스레드를 중지 시킨다
                if (!GlobalApplication.getThread()) {
                    break;
                }
                //stopped 가 false일시 무한 반복 -> 어린이 안전벨트가 풀려있는 동안
                while (!stopped) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // 음악파일 실행 (음원을 미리 저장해놓고 실행하는 방식이 부담을 덜준다)
                            mediaPlayer = MediaPlayer.create(StartActivity.this, R.raw.beep);
                            mediaPlayer.start();
                        }
                    });

                    try {
                        //0.35초 주기 (음원이 현재 0.3초 정도 됨)
                        Thread.sleep(350);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 미디어가 중첩되지 않게 정지 후
                    mediaPlayer.stop();
                    // 초기화 해준다
                    mediaPlayer.reset();
                }
                try {
                    // 0.15초의 락을 걸어준다 (큰 이유는 없음 -> 너무 데이터 전송이 빠르고 잦아서)
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }// while
        }
    }//end of playBeep

    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 사용자 위치 권한 획득
    // 위치 권한은 사용자가 마시멜로우 이상일시에 사용 가능하다.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED&& ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) { }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------
    // 위치 리스너 선언
    protected final LocationListener mLocationListener = new LocationListener() {

        //여기서 위치값이 갱신되면 이벤트가 발생한다.
        //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
        public void onLocationChanged(Location location) {

            longitude = location.getLongitude(); //경도
            latitude = location.getLatitude();   //위도
            status = location.getProvider(); // 제공자
            Log.e("STATUS", status);
            speed = location.getSpeed(); // 속도

            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.

            // 전역변수 객체 선언
            GlobalApplication myApp = (GlobalApplication) getApplication();


            // 현 경/위도 에 대한 이벤트 함수
            location_chk(latitude, longitude);


            // 현재 경도 위도를 전역 변수에 저장한다
            GlobalApplication.setLongitude(longitude);
            GlobalApplication.setLatitude(latitude);

        }

        // 리스너 제공자 변경 및 이벤트 수행 유무
        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    //--------------------------------------------------------------------------------------------------------------------------------------------
    void location_chk(Double latitude, Double longitude) {

        // 전역변수 객체 선언
        GlobalApplication myApp = (GlobalApplication) getApplication();
        route_check = GlobalApplication.getRoute_check();

        Location location_before = new Location("1");
        location_before.setLatitude(GlobalApplication.getLatitude());
        location_before.setLatitude(GlobalApplication.getLongitude());
        Location location_after = new Location("2");
        location_after.setLatitude(latitude);
        location_after.setLatitude(longitude);

        double distance = location_before.distanceTo(location_after);


        //만약 변경된 위치와 변경전 위치가 5m이상 차이 난다면 운행 시작, 운행 시작후에도 계속 메인 엑티비티가 뜨면 안되니까 moving_check 값 변경해준다
        //변경전 위치가 출발위치(유치원) 경/위도와 15m 이내에 들어온다면
        if(GlobalApplication.getLatitude()!=0.0 && GlobalApplication.getLongitude()!=0.0 && distance>5.00 && GlobalApplication.getMoving_check()==0
                && Double.valueOf(Route_data.get(0)[2]) + 0.00015 > GlobalApplication.getLatitude() && Double.valueOf(Route_data.get(0)[2]) - 0.00015 < GlobalApplication.getLatitude()
                && Double.valueOf(Route_data.get(0)[3]) + 0.00015 > GlobalApplication.getLongitude() && Double.valueOf(Route_data.get(0)[3]) - 0.00015 < GlobalApplication.getLongitude()
                && GlobalApplication.getTest_check() == 0){
            Log.e(distance+"m", "이동하였습니다.");
            GlobalApplication.setMoving_check(1);
            GlobalApplication.setRoute_check(GlobalApplication.getRoute_check()+1);
            // activity 이동
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
            startActivity(intent);
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void get_course(){
        String course_info;
        GlobalApplication myApp = (GlobalApplication) getApplication();
        course_info = myApp.getCourse();
        Course.addAll(Arrays.asList(course_info.split("<br>")));
        for (int i = 0; i < Course.size(); i++) {
            Course_data.add(Course.get(i).split(","));
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 받아온 목적지 문자열을 목적지 배열에 입력
    public void get_destination() {
        String destination_info;
        GlobalApplication myApp = (GlobalApplication) getApplication();
        destination_info = GlobalApplication.getDestination();
        Destination.addAll(Arrays.asList(destination_info.split("<br>")));
        for (int i = 0; i < Destination.size(); i++) {
            Destination_data.add(Destination.get(i).split(","));
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void get_student(){
        String student_info;
        GlobalApplication myApp = (GlobalApplication) getApplication();

        student_info = GlobalApplication.getStudent();
        Student.addAll(Arrays.asList(student_info.split("<br>")));


        for (int i = 0; i < Student.size(); i++) {
            Student_data.add(Student.get(i).split(","));
        }

        try{
            // 현재 출석 페이지에 해당 유치원의 모든 아이들을 불러옴
            // 선택된 차량의 경로에 맞는 아이들만 불러와야함
            for(int i = 0; i < Student.size(); i++){
                Log.e("type_num1", type_num1+"");
                Log.e("1.Student_data",Student_data.get(i)[type_num1]);
                Log.e("2.Selected_vehicle", GlobalApplication.getSelected_vehicle().get(type_num2));
                if(Student_data.get(i)[type_num1].equals(GlobalApplication.getSelected_vehicle().get(type_num2))){
                    Log.e("선택된 학생있음","--------------------------");

                    Selected_student.add(Student_data.get(i));
                }
            }
            GlobalApplication.setSelected_student(Selected_student);
            //아이들 상태
            Children_arrive = new int[Selected_student.size()];

            for(int i=0; i<Selected_student.size(); i++){
                Log.e("Selected_student",Selected_student.get(i)[0]);
            }
        } catch (IndexOutOfBoundsException e){

            // 다이얼로그 빌더 선언
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);

            // 다이얼로그 레이아웃 ui 선언
            View view = inflater.inflate(R.layout.popup_confirm, null);

            // 다이얼로그 버튼 및 텍스트뷰 ui 연결
            Button button = view.findViewById(R.id.btn_confirm);
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
            textView.setText("잘못된 학생 등록 - DB 확인");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 다시 다이얼로그 플래그를 0으로 한다.
                    dialog_flag = 0;

                    // activity 이동
                    Intent intent = new Intent(getApplicationContext(), SelectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 최상위 제외 액티비티 제거
                    startActivity(intent);
                    // 다이얼로그 제거
                    dig.dismiss();
                }
            });
        }


    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void get_module(){
        String module_info;
        GlobalApplication myApp = (GlobalApplication) getApplication();
        module_info = GlobalApplication.getModule();
        Module.addAll(Arrays.asList(module_info.split("<br>")));
        for (int i = 0; i < Module.size(); i++) {
            Module_data.add(Module.get(i).split(","));
        }
    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //한글, 영어, 숫자를 제외한 문자열 삭제
    public static String StringReplace(String str){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        str =str.replaceAll(match, "");
        return str;
    }

    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // 아이들의 상태를 받아온다. (0은 미출석 1은 출석)
    void children_status() {
        GlobalApplication myApp = (GlobalApplication) getApplication();

        // 운행시작시 아무도 안탔으니 모두 0으로 초기화
        if (myApp.getChildren_status() == null) {
            String temp = "0";

            // 모듈의 수 == 아이들 수 (배열이 0부터 시작하는데 모듈번호는 1부터 시작이니 -1값을 해준다.)
            for (int i = 0; i < Selected_student.size() - 1; i++) {
                temp += "0";
            }
            // 상태 저장
            myApp.setChildren_status(temp);
        } else {
            // 상태값이 이미 존재한다면 초기화 없이 받아와 저장 (운행 시작이라 당연히 아무도 안탔겠지만 중간에 핸드폰이 꺼진다 던가 앱이 종료됬을때 를 위한 코드)
            // 그러나 다른 부분의 충돌로 인해 작동 하지 않는다.
            String temp[];
            temp = myApp.getChildren_status().split("");
            Log.e("temp",""+temp);

            try{
                // 임시로 사용하기 위한 temp 배열에서 split시 하나의 공백 인자로 인해 인덱스가 서로 맞지 않는다.
                // 어레이로 사용하는 것이 더욱 효과적일 것이다.
                for (int i = 0; i < Children_arrive.length; i++) {
                    // temp 인덱스에 +1을 해주어야 인덱스가 일치한다.
                    Children_arrive[i] = Integer.parseInt(temp[i + 1]);
                }
            }
            catch (NullPointerException e){
                    Log.e("null","null");
            }catch(ArrayIndexOutOfBoundsException e){
                Log.e("null","null");
            }

        }

    }
    //--------------------------------------------------------------------------------------------------------------------------------------------
    public void Student_num(){
        try{
            GlobalApplication myApp = (GlobalApplication) getApplication();
            int count=0;
            int temp[] = new int[30];
            for(int i=0; i<Route_data.size(); i++){

                for(int j=0; j<Selected_student.size(); j++){
                    Log.e("Route_data.get("+i+")[1]",Route_data.get(i)[1]);
                    Log.e("Student_data.get("+j+")[10]",Selected_student.get(j)[type_num3]);
                    if(Route_data.get(i)[1].equals(Selected_student.get(j)[type_num3])){
                        count++;
                        Log.e("count",count+"");
                    }

                }
                temp[i]=count;
                Log.e("temp["+i+"]",""+temp[i]);
                count=0;
            }
            //유치원에 내리는 아이들이 없으므로 1로 넣어준다
            temp[0]=1;//유치원 시작
            temp[Route_data.size()-1]=1;//유치원 도착
            GlobalApplication.setRoute_status(temp);
            info_check = 0;
        }catch(ArrayIndexOutOfBoundsException e){
            info_check = 1;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);

            // 다이얼로그 레이아웃 ui 선언
            View v = inflater.inflate(R.layout.popup_confirm, null);

            // 다이얼로그 버튼 및 텍스트뷰 ui 연결
            Button button = v.findViewById(R.id.btn_confirm);
            TextView textView = v.findViewById(R.id.tv_content);

            // 다이얼로그 바깥 부분 눌러도 안나가지게 설정
            builder.setCancelable(false);

            // 빌더 레이아웃 연결
            builder.setView(v);

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
            textView.setText("잘못된 경로 등록이거나 \n경로에 등록된 어린이가 없습니다");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 다시 다이얼로그 플래그를 0으로 한다.
                    dialog_flag = 0;

                    // 다이얼로그 제거
                    dig.dismiss();
                }
            });
        }
        GlobalApplication.setTotal_child_num(Selected_student.size());
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GlobalApplication myApp = (GlobalApplication) getApplication();

        // 뒤로가기 버튼 클릭시 위치리스너 종료
        lm.removeUpdates(mLocationListener);
        // 전역변수 초기화 후 운행종료
        myApp.init_before_start();
    }

}
