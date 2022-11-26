package com.kse.lpep.service;

import org.springframework.web.multipart.MultipartFile;

public interface IQuestionService {
    String uploadExperTestFile(int isInput, String caseId, String experId, String groupId, MultipartFile file);

    /*
    获取caseId，为后续test案例插入做准备
     */
    String acquireCaseId();

    void checkParam(Integer isInput, String caseId, String experId, String groupId);

}
