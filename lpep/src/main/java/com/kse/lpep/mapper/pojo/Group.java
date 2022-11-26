package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_group")
public class Group implements Serializable {
    private static final long serialVersionUID = 837797662902151560L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String experId;
    private String title;
    private Timestamp createTime;
}
