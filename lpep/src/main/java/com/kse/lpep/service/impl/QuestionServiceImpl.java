package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kse.lpep.common.exception.FrontEndDataException;
import com.kse.lpep.common.exception.ParamException;
import com.kse.lpep.common.exception.SaveFileIOException;
import com.kse.lpep.common.utility.SavingFile;
import com.kse.lpep.mapper.ICaseMapper;
import com.kse.lpep.mapper.IExperMapper;
import com.kse.lpep.mapper.IGroupMapper;
import com.kse.lpep.mapper.IUserGroupMapper;
import com.kse.lpep.mapper.pojo.Case;
import com.kse.lpep.mapper.pojo.Exper;
import com.kse.lpep.mapper.pojo.Group;
import com.kse.lpep.mapper.pojo.UserGroup;
import com.kse.lpep.service.IQuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystems;
import java.text.ParseException;

@Service
public class QuestionServiceImpl implements IQuestionService {
    @Autowired
    private IExperMapper experMapper;
    @Autowired
    private IGroupMapper groupMapper;
    @Autowired
    private ICaseMapper caseMapper;


    @Transactional
    @Override
    public String acquireCaseId() {
        Case myCase = new Case();
//        myCase.setNumber(number);
        caseMapper.insert(myCase);
        return myCase.getId();
    }

    @Transactional
    @Override
    public String uploadExperTestFile(int isInput, String caseId, String experId, String groupId, MultipartFile file) {
        // 1.校验实验id，组别id和caseId是否正确
//        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id", groupId).eq("exper_id", experId);
//        if(groupMapper.selectOne(queryWrapper) == null || caseMapper.selectById(caseId) == null){
//            throw new FrontEndDataException("前端实验id，组别id，caseId传入错误");
//        }

        // 2.上传文件
        String fileSeparator = FileSystems.getDefault().getSeparator();
        // absolutePath为保存的实验名字
        String absolutePath = experMapper.selectById(experId).getWorkspace() + fileSeparator
                + "test" + fileSeparator;
        String saveName = "";

        // 这里的C后面要替换成linux的某个目录，最好放在属性中配置
//        System.out.println(prePath);
//        absolutePath = prePath + fileSeparator + absolutePath;

//        absolutePath = "c:" + fileSeparator + absolutePath;
        if(isInput == 1){
            absolutePath = absolutePath + "input";
            saveName = caseId + ".in";
        }else{
            absolutePath = absolutePath + "std-out";
            saveName = caseId + ".out";
        }
        try {
            SavingFile.saveFile(file, saveName, absolutePath);
        }catch (NullPointerException e){
            throw new NullPointerException("文件为空");
        }catch (SaveFileIOException e1){
            throw new SaveFileIOException("保存过程IO出错");
        }
        return saveName;
    }

    @Override
    public void checkParam(Integer isInput, String caseId, String experId, String groupId) {
        if(isInput != 0 && isInput != 1){
            throw new ParamException("文件输入输出类型只能为0或1");
        }
        if(StringUtils.isBlank(caseId)){
            throw new ParamException("文件caseId不能为空");
        }
        if(StringUtils.isBlank(experId)){
            throw new ParamException("文件experId不能为空");
        }
        if(StringUtils.isBlank(groupId)){
            throw new ParamException("文件groupId不能为空");
        }
    }
}
