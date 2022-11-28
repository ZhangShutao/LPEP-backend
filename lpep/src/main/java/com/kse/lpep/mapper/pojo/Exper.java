package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Accessors(chain = true)
@TableName("t_exper")
@NoArgsConstructor
public class Exper implements Serializable{
    private static final long serialVersionUID = -4973091903659148957L;

    public static final Integer PREPARING = 0;
    public static final Integer ENDED = 1;
    public static final Integer RUNNING = 2;

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
