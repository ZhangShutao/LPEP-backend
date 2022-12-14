package com.kse.lpep.common.utility;

import com.kse.lpep.common.exception.SaveFileIOException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class SavingFile {
    /**
     * 工具类保存文件
     * 例如要將文件test.pdf存到c:/train下，
     * @param file 保存的文件
     * @param saveName 文件要保存成什么名字 test.pdf
     * @param savePath 文件保存的路径 c:/train
     * @return true成功
     */
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
            System.out.println(files.getAbsolutePath());

            file.transferTo(files.toPath().toAbsolutePath());
            // 下面这个会报错，加个C会成功，如果files使用的是相对路径
//            file.transferTo(files);
        }catch (IOException e){
            throw new SaveFileIOException(e.getMessage());
        }
        return true;
    }
}
