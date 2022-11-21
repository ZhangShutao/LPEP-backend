package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("t_phase")
public class Phase {
    @TableId
    private String id;
    private String name;
    private String experId;
    private Integer phaseNumber;
    private Integer type;
}
