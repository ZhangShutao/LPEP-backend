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
@TableName("t_exper")
public class Exper implements Serializable {
    private static final long serialVersionUID = -4973091903659148957L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String title;
    private String creator;
    private Timestamp createTime;
    private Timestamp startTime;
    private String confPath;
    private Integer state;
    private String workspace;
}
