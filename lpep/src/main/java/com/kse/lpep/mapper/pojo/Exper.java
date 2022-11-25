package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("t_exper")
public class Exper {

    public static final Integer PREPARING = 0;
    public static final Integer ENDED = 1;
    public static final Integer RUNNING = 2;

    private String id;
    private String title;
    private String creator;
    private Timestamp createTime;
    private Timestamp startTime;
    private String confPath;
    private Integer state;

    private String workspace;
}
