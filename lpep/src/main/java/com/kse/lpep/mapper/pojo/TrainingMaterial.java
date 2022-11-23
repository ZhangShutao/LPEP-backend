package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_training_material")
public class TrainingMaterial implements Serializable {
    private static final long serialVersionUID = -5706973623391659830L;
    @TableId
    private String id;
    private String groupId;
    private String title;
    private String absolutePath;
    private Timestamp lastUpdateTime;
}
