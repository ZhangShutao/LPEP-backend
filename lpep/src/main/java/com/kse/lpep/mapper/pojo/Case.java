package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("t_case")
public class Case implements Serializable {
    private static final long serialVersionUID = -25274270080678977L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String progQuestionId;
    private Integer number;
}
