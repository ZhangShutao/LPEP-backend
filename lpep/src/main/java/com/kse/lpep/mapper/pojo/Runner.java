package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@TableName("t_runner")
@NoArgsConstructor
public class Runner implements Serializable {
    private static final long serialVersionUID = 7125209994634088479L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String exePath;
    private String command;
}
