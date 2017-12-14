package bhw1899.paridae.model.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextFileReader {
    synchronized public static String read(String path, String fileName) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        String str = "";
        try {
            File urlFile = new File(path, fileName);
            isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            br = new BufferedReader(isr);
            String mimeTypeLine = null;
            while ((mimeTypeLine = br.readLine()) != null) {
                str = str + mimeTypeLine + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return str;
    }
}
