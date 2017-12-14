package bhw1899.paridae.model.utils;

public class FileNameUtils {

    public static String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index > 0) {
            return fileName.substring(index + 1).toLowerCase();
        }
        return null;
    }
}
