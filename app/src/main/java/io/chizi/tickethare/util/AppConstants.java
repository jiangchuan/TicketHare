package io.chizi.tickethare.util;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public interface AppConstants {
    static final String sdcarddir = "/sdcard/" + MRCarUtil.ApplicationDir;

    static final int REQUEST_PERMISSIONS = 401;

    static final int REQUEST_SIGNUP = 20;
    static final int REQUEST_UPDATE_PROFILE = 21;
    static final int REQUEST_CLOSE_IMG_CAPTURE = 22;
    static final int REQUEST_FAR_IMG_CAPTURE = 23;
    static final int REQUEST_TICKET_IMG_CAPTURE = 24;
    static final int REQUEST_QRCODE_SCAN = 25;
    static final int REQUEST_LOCATION_SHOW = 26;
    static final int REQUEST_PREVIEW_SHOW = 27;
    static final int REQUEST_DATABASE_SHOW = 28;

    static final int TYPE_NOT_CONNECTED = 30;
    static final int TYPE_WIFI = 31;
    static final int TYPE_MOBILE = 32;

    static final int TRANS_IMAGE_W = 800;
    static final int TRANS_IMAGE_H = 800;
    static final int COMPRESS_RATIO = 80;

    static final long SECOND_IN_MS = 1000; // in ms
    static final long MINUTE_IN_MS = 60 * SECOND_IN_MS; // in ms
    static final long HOUR_IN_MS = 60 * MINUTE_IN_MS; // in ms
    static final long TICKET_DURATION = 7;


//    static final double CHINA_WEST = 73.0;
//    static final double CHINA_EAST = 135.0;
//    static final double CHINA_SOUTH = 20.0;
//    static final double CHINA_NORTH = 54.0;

    static final double CHINA_WEST = -125.0;
    static final double CHINA_EAST = -64.0;
    static final double CHINA_SOUTH = 24.0;
    static final double CHINA_NORTH = 49.5;

    static final boolean CUSTOM_IP = false;
    //    static final String HOST_IP = "104.154.251.38";
//    static final String HOST_IP = "10.217.76.4";
//    static final String HOST_IP = "192.168.0.18";
    static final String HOST_IP = "192.168.1.100";
    static final int PORT = 50051;

    static final String POLICE_USER_ID = "police_user_id";
    static final String POLICE_PASSWORD = "police_password";
    static final String SET_IP_ADDRESS = "set_ip_address";

    static final String SCANNED_QR_CODE = "scanned_qr_code";
    static final String CURRENT_USER_ID = "current_user_id";
    static final String GOBACK_USER_ID = "goback_user_id";
    static final String CURRENT_POLICE_NAME = "current_police_name";
    static final String CURRENT_POLICE_CITY = "current_police_city";
    static final String CURRENT_POLICE_DEPT = "current_police_dept";
    static final String CURRENT_POLICE_PORTRAIT_PATH = "current_police_portrait_path";
    static final String CURRENT_TICKET_ID = "current_ticket_id";
    static final String CURRENT_LICENSE_NUM = "current_license_num";
    static final String CURRENT_LICENSE_COLOR = "current_license_color";
    static final String CURRENT_IS_UPLOADED = "current_is_uploaded";
    static final String CURRENT_VEHICLE_TYPE = "current_vehicle_type";
    static final String CURRENT_VEHICLE_COLOR = "current_vehicle_color";
    static final String CURRENT_ADDRESS = "current_address";
    static final String CURRENT_LONGITUDE = "current_longitude";
    static final String CURRENT_LATITUDE = "current_latitude";

    static final String MAP_FILE_PATH = "map_file_path";
    static final String FAR_IMG_FILE_PATH = "far_img_file_path";
    static final String CLOSE_IMG_FILE_PATH = "close_img_file_path";

    static final String BACK_LICENSE_NUM = "back_license_num";
    static final String BACK_VEHICLE_COLOR = "back_vehicle_color";
    static final String BACK_VEHICLE_TYPE = "back_vehicle_type";
    static final String BACK_LICENSE_COLOR = "back_license_color";

    static final String NUM_RECORD_TICKET = "num_record_ticket";

    static final String SAVED_INSTANCE_CURR_POS = "saved_instance_curr_pos";
    static final String SAVED_INSTANCE_CURR_INDEX = "saved_instance_curr_index";
    static final String TITLES_FRAGMENT_TICKET_ID = "titles_fragment_ticket_id";
    static final String SAVED_INSTANCE_TICKET_ID = "saved_instance_ticket_id";
    static final String SAVED_INSTANCE_USER_ID = "saved_instance_user_id";
    static final String SAVED_INSTANCE_IP_ADDRESS = "saved_instance_ip_address";
    static final String SAVED_INSTANCE_POLICE_NAME = "saved_instance_police_name";
    static final String SAVED_INSTANCE_POLICE_CITY = "saved_instance_police_city";
    static final String SAVED_INSTANCE_POLICE_DEPT = "saved_instance_police_dept";
    static final String SAVED_INSTANCE_POLICE_PORTRAIT_PATH = "saved_instance_police_portrait_path";
    static final String SAVED_INSTANCE_LICENSE_NUM = "saved_instance_license_num";
    static final String SAVED_INSTANCE_LICENSE_COLOR = "saved_instance_license_color";
    static final String SAVED_INSTANCE_IS_UPLOADED = "saved_instance_is_uploaded";
    static final String SAVED_INSTANCE_VEHICLE_TYPE = "saved_instance_vehicle_type";
    static final String SAVED_INSTANCE_VEHICLE_COLOR = "saved_instance_vehicle_color";
    static final String SAVED_INSTANCE_CURR_TIME = "saved_instance_curr_time";
    static final String SAVED_INSTANCE_YEAR = "saved_instance_year";
    static final String SAVED_INSTANCE_MONTH = "saved_instance_month";
    static final String SAVED_INSTANCE_WEEK = "saved_instance_week";
    static final String SAVED_INSTANCE_DAY = "saved_instance_day";
    static final String SAVED_INSTANCE_HOUR = "saved_instance_hour";
    static final String SAVED_INSTANCE_MINUTE = "saved_instance_minute";
    static final String SAVED_INSTANCE_TIME_MILIS = "saved_instance_time_milis";
    static final String SAVED_INSTANCE_ADDRESS = "saved_instance_address";
    static final String SAVED_INSTANCE_TRUE_ADDRESS = "saved_instance_true_address";
    static final String SAVED_INSTANCE_LONGITUDE = "saved_instance_longitude";
    static final String SAVED_INSTANCE_LATITUDE = "saved_instance_latitude";
    static final String SAVED_INSTANCE_CURR_MAP_PATH = "saved_instance_curr_map_path";
    static final String SAVED_INSTANCE_CURR_IMG1_PATH = "saved_instance_curr_img1_path";
    static final String SAVED_INSTANCE_CURR_IMG2_PATH = "saved_instance_curr_img2_path";
    static final String SAVED_INSTANCE_CURR_IMG3_PATH = "saved_instance_curr_img3_path";

    static final String RUNTIME_DATA_DIR_ASSET = "runtime_data";
    static final String ANDROID_DATA_DIR = "/data/data/org.openalpr.app";
    static final String PREF_INSTALLED_KEY = "installed";
    static final String MAP_IMG_FILE_PREFIX = "_map";
    static final String CLOSE_IMG_FILE_PREFIX = "_close";
    static final String FAR_IMG_FILE_PREFIX = "_far";
    static final String TICKET_IMG_FILE_PREFIX = "_ticket";
    static final String PNG_FILE_SUFFIX = ".png";
    static final String JPEG_FILE_SUFFIX = ".jpg";

    static final String FILE_INSDCARD_DIR = "mrcar";
}
