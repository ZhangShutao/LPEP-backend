package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_training_material")
public class TrainingMaterial {
    private String id;
    private String groupId;
    private String title;
    private String absolutePath;
    private Timestamp createTime;
}
