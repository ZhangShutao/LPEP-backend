package com.kse.lpep.service;

import com.kse.lpep.controller.vo.AddNonProgQuestionInfo;
import com.kse.lpep.controller.vo.CreateGroupInfo;
import com.kse.lpep.controller.vo.CreatePhaseInfo;
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
    添加类型1问题，这里选择相信前端
     */
    int addQuestionTypeNonProg(String experId, String groupName, String phaseNumber,
                                   List<AddNonProgQuestionInfo> addNonProgQuestionInfoList);
}
