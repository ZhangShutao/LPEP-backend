package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_group")
public class Group {
    private String id;
    private String experId;
    private String title;
    private Timestamp createTime;
    private String trainingMaterialId;
}
