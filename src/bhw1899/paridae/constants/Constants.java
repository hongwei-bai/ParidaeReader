package bhw1899.paridae.constants;

import java.io.File;
import java.util.ArrayList;

public class Constants {
    final public static String SD_CARD_PATH = "storage/sdcard1";
    final public static String DEFAULT_TXT_PATH = SD_CARD_PATH + File.separator + "paridae"
            + File.separator;
    public static ArrayList<String> SUPPORT_FORMAT_ARRAYLIST = null;
    static {
        SUPPORT_FORMAT_ARRAYLIST = new ArrayList<String>();
        SUPPORT_FORMAT_ARRAYLIST.add("txt");
    }
    final public static String PLAYING_STRING = " [playing...]";
    final public static String INTENT_KEY_FILE_NAME = "filename";
}
