package com.kse.lpep.service.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
public class TrainingMaterialInfo {
    // 培训材料主键
    private String id;
    // 教材名字
    private String title;
    // 实验名字
    private String experName;
//    private String absolutePath;
    private String lastUpdateTime;
}
