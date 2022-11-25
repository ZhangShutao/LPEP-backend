package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_phase")
public class Phase {

    public static final Integer QUESTIONNAIRE = 0;
    public static final Integer PROGRAMING = 1;

    private String id;
    private String name;
    private String experId;
    private Integer phaseNumber;
    private Integer type;
}
