package helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {

    public static void writeString(String path, String s){
        writeString(new File(path),s);
    }

    public static void writeString(File file, String s) {
        try {
            writeString(new FileWriter(file),s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeString(FileWriter bw, String s) {
        try {
            bw.write(s + "\n");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}