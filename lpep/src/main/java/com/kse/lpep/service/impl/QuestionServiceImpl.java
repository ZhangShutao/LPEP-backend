package com.kse.lpep.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kse.lpep.common.exception.FrontEndDataException;
import com.kse.lpep.common.exception.SaveFileIOException;
import com.kse.lpep.common.utility.SavingFile;
import com.kse.lpep.mapper.ICaseMapper;
import com.kse.lpep.mapper.IExperMapper;
import com.kse.lpep.mapper.IGroupMapper;
import com.kse.lpep.mapper.IUserGroupMapper;
import com.kse.lpep.mapper.pojo.Case;
import com.kse.lpep.mapper.pojo.Group;
import com.kse.lpep.mapper.pojo.UserGroup;
import com.kse.lpep.service.IQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.FileSystems;

@Service
public class QuestionServiceImpl implements IQuestionService {
    @Autowired
    private IExperMapper experMapper;
    @Autowired
    private IGroupMapper groupMapper;
    @Autowired
    private ICaseMapper caseMapper;

    @Override
    public String acquireCaseId(int number) {
        Case myCase = new Case();
        myCase.setNumber(number);
        caseMapper.insert(myCase);
        return myCase.getId();
    }

    @Override
    public String uploadExperTestFile(int isInput, String caseId, String experId, String groupId, MultipartFile file) {
        // 1.校验实验id，组别id和caseId是否正确
        QueryWrapper<Group> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", groupId).eq("exper_id", experId);
//        Case myCase = caseMapper.selectById(caseId);
        if(groupMapper.selectOne(queryWrapper) == null || caseMapper.selectById(caseId) == null){
            throw new FrontEndDataException("前端实验id，组别id，caseId传入错误");
        }

        // 2.上传文件
        String fileSeparator = FileSystems.getDefault().getSeparator();
        String absolutePath = experMapper.selectById(experId).getWorkspace() + fileSeparator + "test" + fileSeparator;
        String saveName = "";
        if(isInput == 1){
            absolutePath = absolutePath + fileSeparator + "input";
            saveName = caseId + ".in";
        }else{
            absolutePath = absolutePath + fileSeparator + "std-out";
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
}
