package inno.i.Activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import inno.i.GlobalApplication;
import inno.i.R;

public class DestinationActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 위도 경도를 담을 변수 static 이 코드 전체에 부담을 준다.
    // 가능 하면 지역 변수로 그리고 클래스 마다 간섭하지 못하게 하는것이 좋다.
    static double latitude;
    static double longitude;
    // 속도를 담을 변수
    static double speed;
    // 위치 제공자가 누군지를 담을 변수 (실내면 네트워크 실외면 gps) 이 값은 현재 테스트를 위해 운행 기록 데이터에 올라간다.
    static String status;


    EditText destination_name_et;//목적지이름

//    EditText latitude_et;//경도
//    EditText longitude_et;//위도
    Button location_btn;//경도,위도 입력버튼
    Button enter_btn;//목적지 등록버튼
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;



    //--------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);


        //UI
        destination_name_et = findViewById(R.id.destination_name);
//        latitude_et = findViewById(R.id.latitude);
//        longitude_et = findViewById(R.id.longitude);
        location_btn = findViewById(R.id.destination_search);
        enter_btn = findViewById(R.id.enter_btn);


        // 현 위치를 저장하는 위치 리스너
        // LocationManager 객체를 얻어온다
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

          // 사용자의 위치제공 권한 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // GPS 위치 제공자
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                100, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
        // Network 위치 제공자
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                100, // 통지사이의 최소 시간간격 (miliSecond)
                0, // 통지사이의 최소 변경거리 (m)
                mLocationListener);


      location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                latitude_et.setText(Double.toString(latitude));
//                longitude_et.setText(Double.toString(longitude));
                gogoMap(latitude, longitude);


            }
        });

        //레이아웃 프레그먼트의 핸들을 가져옴
        SupportMapFragment mapfragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapfragment.getMapAsync(this);

        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String str="https://innoi.co.kr/safe-school-og/test/app/insert_location.php?des_name=";
                str += destination_name_et.getText().toString();
                str += "&school_name=";
                str += GlobalApplication.getSchool_name();
                str += "&latitude=";
                str += Double.parseDouble(String.format("%.6f",latitude));
                str += "&longitude=";
                str += Double.parseDouble(String.format("%.6f",longitude));
                putData(str);

                String msg = destination_name_et.getText().toString()+" 목적지 등록이 완료되었습니다.";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------
    // 위치 리스너 선언
    protected final LocationListener mLocationListener = new LocationListener() {

        //여기서 위치값이 갱신되면 이벤트가 발생한다.
        //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
        public void onLocationChanged(Location location) {
            longitude = Double.parseDouble(String.format("%.6f",location.getLongitude())); //경도
            latitude = Double.parseDouble(String.format("%.6f",location.getLatitude()));   //위도
            status = location.getProvider(); // 제공자

            Log.e("longitude>>","" + Double.parseDouble(String.format("%.6f",latitude)));
            Log.e("latitude>>","" + Double.parseDouble(String.format("%.6f",latitude)));
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

    //--------------------------------------------------------------------------------------------------------------------------------------------------
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

    //맵이 사용할 준비가 되었을 때 호출되어지는 메소드
   @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        LatLng location = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location); //마커가 표시될 위치
        markerOptions.title("현재위치");//마커 클릭시 보여주는 설명
        map.addMarker(markerOptions);//GoogleMap객체에 추가

        //카메라를 지정한 경도, 위도로 이동함
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
        //지정한 단계로 카메라 줌을 조정함. 숫자가 커질 수록 상세지도
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    public void gogoMap(double latitude, double longitude) {


        // 맵 위치를 이동하기

        CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));

        // 위도,경도

        map.moveCamera(update);


        // 보기 좋게 확대 zoom 하기

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
        map.animateCamera(zoom);


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude, longitude)); //마커가 표시될 위치
        markerOptions.title("현재위치");//마커 클릭시 보여주는 설명
        map.addMarker(markerOptions);//GoogleMap객체에 추가

    }
}
