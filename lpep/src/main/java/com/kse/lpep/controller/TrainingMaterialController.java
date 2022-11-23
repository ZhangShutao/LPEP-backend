package com.kse.lpep.controller;

import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.mapper.ITrainingMaterialMapper;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.dto.ExperGroupInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("train")
public class TrainingMaterialController {
    @Autowired
    private ITrainingMaterialMapper trainingMaterialMapper;
    @Autowired
    private ITrainingMaterialService trainingMaterialService;


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
//            String finalPath = "/" + filePath + ".pdf";
            InputStream is = new FileInputStream(filePath);
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

    @GetMapping("/getexpergroup")
    public BaseResponse getExperAndGroup(){
        BaseResponse response = new BaseResponse();
        response.setStatus(200).setMsg("返回所有实验和组别");
        List<ExperGroupInfo> data = trainingMaterialService.queryAllExperGroup();
        response.setData(data);
        return response;
    }

    // 自己测试文件上传功能
    @PostMapping("/upload")
    public BaseResponse testUpload(@RequestParam(value = "file") MultipartFile file){
        BaseResponse response = new BaseResponse();
        response.setStatus(200);
        String workspace = "c:/train";
        try{
            File files = new File(workspace, file.getOriginalFilename());
            file.transferTo(files);
            response.setMsg("上传成功");
        }catch (IOException e){
            response.setMsg("上传失败");
//            e.printStackTrace();
        }
        return response;
    }

}


