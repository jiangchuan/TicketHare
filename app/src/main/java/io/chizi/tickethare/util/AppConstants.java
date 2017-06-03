package io.chizi.tickethare.util;

/**
 * Created by Jiangchuan on 5/21/17.
 */

public interface AppConstants {
    static final int REQUEST_SIGNUP = 0;
    static final int REQUEST_UPDATE_PROFILE = 1;
    static final int REQUEST_CLOSE_IMG_CAPTURE = 2;
    static final int REQUEST_FAR_IMG_CAPTURE = 3;
    static final int REQUEST_TICKET_IMG_CAPTURE = 4;
    static final int REQUEST_QRCODE_SCAN = 5;
    static final int REQUEST_LOCATION_SHOW = 6;
    static final int REQUEST_PREVIEW_SHOW = 7;
    static final int REQUEST_DATABASE_SHOW = 8;

    static final int TYPE_NOT_CONNECTED = 10;
    static final int TYPE_WIFI = 11;
    static final int TYPE_MOBILE = 12;

    static final int TRANS_IMAGE_W = 800;
    static final int TRANS_IMAGE_H = 800;
    static final int COMPRESS_RATIO = 80;

//    static final String HOST_IP = "104.154.251.38";
    static final String HOST_IP = "192.168.1.8";
    static final int PORT = 50051;

    static final String POLICE_USER_ID = "police_user_id";
    static final String POLICE_PASSWORD = "police_password";

    static final String SCANNED_QR_CODE = "scanned_qr_code";
    static final String CURRENT_USER_ID = "current_user_id";
    static final String GOBACK_USER_ID = "goback_user_id";
    static final String CURRENT_POLICE_NAME = "current_police_name";
    static final String CURRENT_POLICE_CITY = "current_police_city";
    static final String CURRENT_POLICE_DEPT = "current_police_dept";
    static final String CURRENT_TICKET_ID = "current_ticket_id";
    static final String CURRENT_LICENSE_NUM = "current_license_num";
    static final String CURRENT_LICENSE_COLOR = "current_license_color";
    static final String CURRENT_LICENSE_CORRECT = "current_license_correct";
    static final String CURRENT_VEHICLE_TYPE = "current_vehicle_type";
    static final String CURRENT_VEHICLE_COLOR = "current_vehicle_color";
    static final String CURRENT_ADDRESS = "current_address";
    static final String CURRENT_LONGITUDE = "current_longitude";
    static final String CURRENT_LATITUDE = "current_latitude";

    static final String CURRENT_MAP_FILE_PATH = "current_map_file_path";
    static final String CURRENT_IMG1_FILE_PATH = "current_img1_file_path";
    static final String CURRENT_IMG2_FILE_PATH = "current_img2_file_path";
    static final String CURRENT_IMG3_FILE_PATH = "current_img3_file_path";
    static final String CURRENT_MAP_LONGITUDE = "current_map_longitude";
    static final String CURRENT_MAP_LATITUDE = "current_map_latitude";
    static final String CURRENT_MAP_ADDRESS = "current_map_address";

    static final String BACK_VEHICLE_COLOR = "back_vehicle_color";
    static final String BACK_VEHICLE_TYPE = "back_vehicle_type";
    static final String BACK_LICENSE_COLOR = "back_license_color";

    static final String NUM_RECORD_TICKET = "num_record_ticket";

    static final String SAVED_INSTANCE_MAP = "saved_instance_map";
    static final String SAVED_INSTANCE_PHOTO1 = "saved_instance_image1";
    static final String SAVED_INSTANCE_PHOTO2 = "saved_instance_image2";
    static final String SAVED_INSTANCE_CURR_POS = "saved_instance_curr_pos";
    static final String SAVED_INSTANCE_CURR_INDEX = "saved_instance_curr_index";
    static final String SAVED_INSTANCE_TICKET_ID = "saved_instance_ticket_id";
    static final String SAVED_INSTANCE_USER_ID = "saved_instance_user_id";
    static final String SAVED_INSTANCE_POLICE_NAME = "saved_instance_police_name";
    static final String SAVED_INSTANCE_POLICE_CITY = "saved_instance_police_city";
    static final String SAVED_INSTANCE_POLICE_DEPT = "saved_instance_police_dept";
    static final String SAVED_INSTANCE_LICENSE_NUM = "saved_instance_license_num";
    static final String SAVED_INSTANCE_LICENSE_COLOR = "saved_instance_license_color";
    static final String SAVED_INSTANCE_LICENSE_CORRECT = "saved_instance_license_correct";
    static final String SAVED_INSTANCE_VEHICLE_TYPE = "saved_instance_vehicle_type";
    static final String SAVED_INSTANCE_VEHICLE_COLOR = "saved_instance_vehicle_color";
    static final String SAVED_INSTANCE_CURR_TIME = "saved_instance_curr_time";
    static final String SAVED_INSTANCE_YEAR = "saved_instance_year";
    static final String SAVED_INSTANCE_MONTH = "saved_instance_month";
    static final String SAVED_INSTANCE_DAY = "saved_instance_day";
    static final String SAVED_INSTANCE_HOUR = "saved_instance_hour";
    static final String SAVED_INSTANCE_MINUTE = "saved_instance_minute";
    static final String SAVED_INSTANCE_ADDRESS = "saved_instance_address";
    static final String SAVED_INSTANCE_LONGITUDE = "saved_instance_longitude";
    static final String SAVED_INSTANCE_LATITUDE = "saved_instance_latitude";
    static final String SAVED_INSTANCE_CURR_MAP_PATH = "saved_instance_curr_map_path";
    static final String SAVED_INSTANCE_CURR_IMG1_PATH = "saved_instance_curr_img1_path";
    static final String SAVED_INSTANCE_CURR_IMG2_PATH = "saved_instance_curr_img2_path";
    static final String SAVED_INSTANCE_CURR_IMG3_PATH = "saved_instance_curr_img3_path";

    static final String RUNTIME_DATA_DIR_ASSET = "runtime_data";
    static final String ANDROID_DATA_DIR = "/data/data/org.openalpr.app";
    static final String OPENALPR_CONF_FILE = "openalpr.conf";
    static final String PREF_INSTALLED_KEY = "installed";
    static final String MAP_FILE_PREFIX = "MAP_";
    static final String CLOSE_IMG_FILE_PREFIX = "CLOSE_";
    static final String FAR_IMG_FILE_PREFIX = "FAR_";
    static final String TICKET_IMG_FILE_PREFIX = "TICKET_";
    static final String PNG_FILE_SUFFIX = ".png";
    static final String JPEG_FILE_SUFFIX = ".jpg";
    static final String ALPR_FRAGMENT_TAG = "alpr";
    static final String ALPR_ARGS = "alprargs";

    static final String FILE_INSDCARD_DIR = "mrcar";
}
