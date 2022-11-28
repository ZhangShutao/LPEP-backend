package com.kse.lpep;

import com.kse.lpep.common.exception.SaveFileIOException;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class FileTest {
    @Test
    public void saveFile(){
        File file = new File("/abc");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write("abcdef");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static boolean saveFile(MultipartFile file, String saveName, String savePath){
        if(file.isEmpty()){
            throw new NullPointerException("文件为空");
        }
        try{
            File files = new File(savePath, saveName);
            File parentFile = files.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            file.transferTo(files);
        }catch (IOException e){
            throw new SaveFileIOException(e.getMessage());
        }
        return true;
    }
}
