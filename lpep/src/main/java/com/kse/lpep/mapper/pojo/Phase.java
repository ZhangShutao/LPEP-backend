package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("t_phase")
public class Phase implements Serializable {
    private static final long serialVersionUID = -3253916079402647284L;
    public static final Integer QUESTIONNAIRE = 0;
    public static final Integer PROGRAMING = 1;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String experId;
    private Integer phaseNumber;
    private Integer type;
}
