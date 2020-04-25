package inno.i;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class GlobalApplication extends Application {

    public static Context context;

    //현재 유치원
    private static String School_name;
    //현재 사용자 정보
    private static String User;
    //선생님 정보
    private static String Teacher;
    //차량 정보
    private static String Vehicle;
    //학생 정보
    private static String Student;
    //목적지 정보
    private static String Destination;
    //경로 정보
    private static String Course;
    //모듈 정보
    private static String Module;



    //선택된 차량 정보(selectactivity의 스피너에서)
    private static ArrayList<String> selected_vehicle;
    //선택된 선생님 정보
    private static ArrayList<String> selected_teacher;
    //선택된 유형(등원/하원)
    private static String selected_type;
    //선택된 경로의 목적지 정보가 담겨있는 배열
    private static ArrayList<String[]> selected_route;
    //선택된 경로의 승/하차지점 마다 있는 학생 수
    private static int[] route_status = new int[30];
    //선택된 차량에 맞는 아이들
    private static ArrayList<String[]> selected_student;


    //현재 경도
    private static double latitude;
    //현재 위도
    private static double longitude;

    //스레드
    private static Boolean thread = true;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    //움직임여부체크(운행시작 페이지에서 사용)
    private static int moving_check;
    //경로를 체크하기 위한 변수
    private static int route_check;
    //차량의 운생상태를 체크하기 위한 변수
    private static int vehicle_check;
    //하원 시, 출발 전 출석체크 확인 변수
    private static int start_check;
    //테스트주행일 시 1, 아니면 0
    private static int test_check=0;


    private static String children_status;
    private static int total_child_num = 0;
    private static int remainder_student = 0;



    private static String Nextlocation;
    private static String NextNextlocation;
    private static String Beforelocation = "출발지점";


    private static ArrayList<Integer> attendance_check = new ArrayList<>();

    private static int Resid;
    private static int Resnum;
    private static String child_name;

    private static int longclick_flag=0;

    //qr코드 체크(세군데)
    private static int qr_check;
    //setter-----------------------------------------------------------------------------------------

    public static void setSchool_name(String school_name) {
        School_name = school_name;
    }
    public static void setUser(String user) {
        User = user;
    }
    public static void setTeacher(String teacher) {
        Teacher = teacher;
    }
    public static void setVehicle(String vehicle) {
        Vehicle = vehicle;
    }
    public static void setStudent(String student) {
        Student = student;
    }
    public static void setDestination(String destination) {
        Destination = destination;
    }
    public static void setCourse(String course) {
        Course = course;
    }
    public static void setModule(String module) {
        Module = module;
    }
    public static void setSelected_vehicle(ArrayList<String> selected_vehicle) {
        GlobalApplication.selected_vehicle = selected_vehicle;
    }
    public static void setSelected_teacher(ArrayList<String> selected_teacher) {
        GlobalApplication.selected_teacher = selected_teacher;
    }
    public static void setSelected_type(String selected_type) {
        GlobalApplication.selected_type = selected_type;
    }
    public static void setSelected_route(ArrayList<String[]> selected_route) {
        GlobalApplication.selected_route = selected_route;
    }
    public static void setLatitude(double latitude) {
        GlobalApplication.latitude = latitude;
    }
    public static void setLongitude(double longitude) {
        GlobalApplication.longitude = longitude;
    }
    public static void setThread(Boolean thread) {
        GlobalApplication.thread = thread;
    }
    public static void setMoving_check(int moving_check) {
        GlobalApplication.moving_check = moving_check; }
    public static void setRoute_check(int route_check) {
        GlobalApplication.route_check = route_check; }
    public static void setVehicle_check(int vehicle_check) {
        GlobalApplication.vehicle_check = vehicle_check; }
    public static void setStart_check(int start_check) {
        GlobalApplication.start_check = start_check; }

    public static void setAttendance_check(ArrayList<Integer> attendance_check) {
        GlobalApplication.attendance_check = attendance_check;
    }

    public static void setChildren_status(String children_status) {
        GlobalApplication.children_status = children_status;
    }
    public static void setTotal_child_num(int total_child_num) {
        GlobalApplication.total_child_num = total_child_num;
    }
    public static void setRemainder_student(int remainder_student) {
        GlobalApplication.remainder_student = remainder_student;
    }
    public void setNextlocation(String Nextlocation) {
        GlobalApplication.Nextlocation = Nextlocation;
    }
    public void setNextNextlocation(String NextNextlocation) {
        GlobalApplication.NextNextlocation = NextNextlocation;
    }
    public void setBeforelocation(String Beforelocation) {
        GlobalApplication.Beforelocation = Beforelocation;
    }
    public void init_before_start() {

        setRoute_check(0);

        setNextlocation(null);
        setNextNextlocation(null);
        setBeforelocation("출발지점");

        //setStatus(0);
        setRemainder_student(0);

        setMoving_check(0);
        setVehicle_check(0);
        setChildren_status(null);
    }

    public static void setResid(int resid) {
        Resid = resid;
    }

    public static void setResnum(int resnum) {
        Resnum = resnum;
    }

    public static void setChild_name(String child_name) {
        GlobalApplication.child_name = child_name;
    }

    public static void setRoute_status(int[] route_status) {
        GlobalApplication.route_status = route_status;
    }

    public static void setLongclick_flag(int longclick_flag) {
        GlobalApplication.longclick_flag = longclick_flag;
    }

    public static void setSelected_student(ArrayList<String[]> selected_student) {
        GlobalApplication.selected_student = selected_student;
    }

    public static void setTest_check(int test_check) {
        GlobalApplication.test_check = test_check;
    }

    public static void setQr_check(int qr_check) {
        GlobalApplication.qr_check = qr_check;
    }
    //getter-----------------------------------------------------------------------------------------

    public static String getSchool_name() {
        return School_name;
    }
    public static String getUser() {
        return User;
    }
    public static String getTeacher() {
        return Teacher;
    }
    public static String getVehicle() {
        return Vehicle;
    }
    public static String getStudent() {
        return Student;
    }
    public static String getDestination() {
        return Destination;
    }
    public static String getCourse() {
        return Course;
    }
    public static String getModule() {
        return Module;
    }
    public static ArrayList<String> getSelected_vehicle() {
        return selected_vehicle;
    }
    public static ArrayList<String> getSelected_teacher() {
        return selected_teacher;
    }
    public static String getSelected_type() {
        return selected_type;
    }
    public static ArrayList<String[]> getSelected_route() {
        return selected_route;
    }
    public static double getLatitude() {
        return latitude;
    }
    public static double getLongitude() {
        return longitude;
    }
    public static Boolean getThread() {
        return thread;
    }
    public static int getMoving_check() {
        return moving_check;
    }
    public static int getRoute_check() {
        return route_check;
    }
    public static int getVehicle_check() { return vehicle_check; }
    public static int getStart_check() {
        return start_check;
    }
    public static ArrayList<Integer> getAttendance_check() {
        return attendance_check;
    }
    public static String getChildren_status() {
        return children_status;
    }
    public static int getTotal_child_num() {
        return total_child_num;
    }
    public static int getRemainder_student() {
        return remainder_student;
    }
    // 전위치,다음위치,다다음 위치
    public String getNextlocation() {
        return Nextlocation;
    }
    public String getNextNextlocation() {
        return NextNextlocation;
    }
    public String getBeforelocation() {
        return Beforelocation;
    }

    public static int getResid() {
        return Resid;
    }

    public static int getResnum() {
        return Resnum;
    }

    public static String getChild_name() {
        return child_name;
    }

    public static int[] getRoute_status() {
        return route_status;
    }

    public static int getLongclick_flag() {
        return longclick_flag;
    }

    public static ArrayList<String[]> getSelected_student() {
        return selected_student;
    }

    public static int getTest_check() {
        return test_check;
    }

    public static int getQr_check() {
        return qr_check;
    }
}

