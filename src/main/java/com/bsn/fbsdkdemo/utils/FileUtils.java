package com.bsn.fbsdkdemo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author kuan
 * Created on 2020/5/23.
 * @description
 */
public class FileUtils {
    public static byte[] readFile(String filePath) throws IOException {
        FileInputStream inputStream  = new FileInputStream(filePath);
        byte[]  fileBytes = new byte[inputStream.available()];
        inputStream.read(fileBytes);
        return fileBytes;
    }

    public static void writeFile(String content,String fileName,boolean append) throws IOException {
        File file = new File(fileName);

        if (!file.getParentFile().exists()){
            file.getParentFile().mkdir();
        }

        FileWriter fileWriter = new FileWriter(fileName,append);
        fileWriter.write(content);
        fileWriter.close();
    }
}
