package com.kse.lpep.service;

import com.kse.lpep.controller.vo.AddNonProgQuestionInfo;
import com.kse.lpep.controller.vo.AddProgQuestionInfo;
import com.kse.lpep.controller.vo.CreateGroupInfo;
import com.kse.lpep.controller.vo.CreatePhaseInfo;
import com.kse.lpep.service.dto.AddProgQuestionDto;
import com.kse.lpep.service.dto.CreateExperResult;
import com.kse.lpep.service.dto.ExperInfo;
import com.kse.lpep.service.dto.NonProgQuestionInfo;

import java.util.List;

public interface IAdminService {
    /**
     * 管理员将用户添加到实验
     */
    int addTesterToExper(String userId, String experId, String groupId);

    CreateExperResult createExper(String creatorId, String experName, String startTime, String workspace,
                                  List<CreateGroupInfo> groupInfoList, List<CreatePhaseInfo> phaseInfoList);

    /*
    添加非编程类型问题，这里选择相信前端
     */
    int addQuestionTypeNonProg(String experId, String groupId, int phaseNumber,
                                   List<AddNonProgQuestionInfo> addNonProgQuestionInfoList);

    /*
    管理员添加编程类型问题
     */
//    int addQuestionTypeProg(AddProgQuestionDto reqDto, List<String> caseIds);

    void addQuestionTypeProg(String experId, String groupId, int phaseNumber,
                            List<AddProgQuestionInfo> addProgQuestionInfoList);
}
