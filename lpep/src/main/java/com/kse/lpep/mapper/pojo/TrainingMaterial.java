package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_training_material")
public class TrainingMaterial {
    @TableId
    private String id;
    private String groupId;
    private String title;
    private String absolutePath;
    private Timestamp lastUpdateTime;
}
