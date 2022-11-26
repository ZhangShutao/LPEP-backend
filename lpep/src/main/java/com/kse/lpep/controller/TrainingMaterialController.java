package com.kse.lpep.controller;


import com.kse.lpep.common.constant.ConstantCode;
import com.kse.lpep.controller.vo.BaseResponse;
import com.kse.lpep.mapper.ITrainingMaterialMapper;
import com.kse.lpep.service.ITrainingMaterialService;
import com.kse.lpep.service.dto.ExperGroupInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.List;

@RestController
@RequestMapping("train")
public class TrainingMaterialController {
    @Autowired
    private ITrainingMaterialMapper trainingMaterialMapper;
    @Autowired
    private ITrainingMaterialService trainingMaterialService;


    /**
     * 下载指定培训文档pdf文档接口
     * 功能介绍：用户查看指定培训文档的pdf
     * 下载成功206;失败216
     * @param fileId
     * @param response
     */
    @GetMapping("/files/{file_id}")
    public void getFile(
            @PathVariable("file_id") String fileId,
            HttpServletResponse response) {
        // 如果field为空，访问不了，这里不需要校验
//        if (StringUtils.isBlank(fileId)) {
//            response.setStatus(ConstantCode.FILE_NAME_IS_BLANK);
//            return;
//        }
        String filePath = trainingMaterialMapper.selectById(fileId).getAbsolutePath();
        // 这里封装不住http状态码
//        if (filePath == null) {
//            response.setStatus(ConstantCode.FILE_NULL_PATH);
//            return;
//        }
//        String fileSeparator = FileSystems.getDefault().getSeparator();
        // 这里打开文件的时候要注意pdf的问题，如果上传的时候
//        filePath = prePath + fileSeparator + filePath;
        try {
            response.setContentType("application/pdf");
            InputStream is = new FileInputStream(filePath);
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream");
        }
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


    /**
     * 管理员获取全部实验和实验对应的组别接口
     * 状态码200成功；没有失败
     * @return List<ExperGroupInfo>
     */
    @GetMapping("/getexpergroup")
    public BaseResponse getExperAndGroup(){
        BaseResponse response = new BaseResponse();
        response.setStatus(ConstantCode.QUERY_SUCCESS).setMsg("返回所有实验和组别");
        List<ExperGroupInfo> data = trainingMaterialService.queryAllExperGroup();
        response.setData(data);
        return response;
    }
}


