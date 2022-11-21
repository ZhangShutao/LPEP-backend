package com.kse.lpep.controller;

import com.kse.lpep.mapper.ITrainingMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("train")
public class TrainingMaterialController {
    @Autowired
    private ITrainingMaterialMapper trainingMaterialMapper;


    @GetMapping("/files/{file_id}")
    public void getFile(
            @PathVariable("file_id") String fileId,
            HttpServletResponse response) {
        System.out.println(fileId);
        String filePath = trainingMaterialMapper.selectById(fileId).getAbsolutePath();
        if(filePath == null){
            throw new NullPointerException();
        }
        try {
            response.setContentType("application/pdf");
            // get your file as InputStream
            String finalPath = "/" + filePath + ".pdf";
            InputStream is = new FileInputStream(finalPath);
            // copy it to response's OutputStream
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
//            log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
//    版本1
//    @GetMapping("/files/{file_name}")
//    public void getFile(
//            @PathVariable("file_name") String fileName,
//            HttpServletResponse response) {
//        try {
//            // get your file as InputStream
//            String path = "/" + fileName;
//            InputStream is = new FileInputStream(path);
//            // copy it to response's OutputStream
//            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
//            response.setContentType("application/pdf");
//            response.flushBuffer();
//        } catch (IOException ex) {
////            log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
//            throw new RuntimeException("IOError writing file to output stream");
//        }

//    源代码
//    @GetMapping("/files")
//    public void getFile(
//            HttpServletResponse response, String s) {
//        try {
//            // get your file as InputStream
//            InputStream is = new FileInputStream("/testIO.pdf");
//            // copy it to response's OutputStream
//            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
//            response.setContentType("application/pdf");
//            response.flushBuffer();
//        } catch (IOException ex) {
////            log.info("Error writing file to output stream. Filename was '{}'", fileName, ex);
//            throw new RuntimeException("IOError writing file to output stream");
//        }

    }

}


