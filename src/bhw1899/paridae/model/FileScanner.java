package bhw1899.paridae.model;

import java.io.File;
import java.util.ArrayList;

import bhw1899.paridae.constants.Constants;
import bhw1899.paridae.model.utils.FileNameUtils;

public class FileScanner {
    public static ArrayList<String> scan() {
        ArrayList<String> list = new ArrayList<String>();
        File dir = new File(Constants.DEFAULT_TXT_PATH);
        File files[] = dir.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            String extension = FileNameUtils.getExtension(fileName);
            if (extension != null && Constants.SUPPORT_FORMAT_ARRAYLIST.contains(extension)) {
                list.add(fileName);
            }
        }
        return list;
    }
}
