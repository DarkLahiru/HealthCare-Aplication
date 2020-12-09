package Common;

import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import TestResult.Upload;

public class Common {

    public static final String KEY_REPORT_SELECTED = "REPORT_SELECTED";
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_DOCTOR_STORE = "DOCTOR_STORE";
    public static final String KEY_LOCATION_LOAD_DONE = "LOCATION_LOAD_DONE";
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_LOCATION_SELECTED = "LOCATION_SELECTED";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final Object DISABLE_TAG = "DISABLE" ;
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static String currentDoctor;
    public static String currentLocation;
    public static int step  = 0;
    public static int currentTimeSlot = -1;
    public static Calendar currentDate =  Calendar.getInstance()  ;
    public static String currentPatient;
    public static Upload currentReport;

    public static String convertTimeSlotToString(int position) {
        switch (position){
            case 0:
                return "9.00 - 9.30";
            case 1:
                return "9.30 - 10.00";
            case 2:
                return "10.00 - 10.30";
            case 3:
                return "10.30 - 11.00";
            case 4:
                return "11.00 - 11.30";
            case 5:
                return "11.30 - 12.00";
            case 6:
                return "12.00 - 12.30";
            case 7:
                return "12.30 - 13.00";
            case 8:
                return "13.00 - 13.30";
            case 9:
                return "13.30 - 14.00";
            case 10:
                return "14.00 - 14.30";
            case 11:
                return "14.30 - 15.00";
            case 12:
                return "15.00 - 15.30";
            case 13:
                return "15.30 - 16.00";
            case 14:
                return "16.00 - 16.30";
            case 15:
                return "16.30 - 17.00";
            case 16:
                return "17.00 - 17.30";
            case 17:
                return "17.30 - 18.00";
            case 18:
                return "18.00 - 18.30";
            case 19:
                return "18.30 - 19.00";
            default:
                return "Closed";
        }
    }
}
