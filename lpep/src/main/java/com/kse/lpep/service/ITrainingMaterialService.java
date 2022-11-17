package com.kse.lpep.service;

import com.kse.lpep.service.dto.TrainingMaterialInfo;

import java.util.List;

public interface ITrainingMaterialService {
    /**
     * 列举所有实验的培训教材
     * @return
     */
//    List<TrainingMaterialInfo> listAllTextbook();

    /**
     * 列举个人实验进行中和实验未开始的参考材料
     * @param userId
     * @return
     */
    List<TrainingMaterialInfo> listPersonalTextbook(String userId);


}
