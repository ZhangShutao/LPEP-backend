package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_runner")
public class Runner {
    private String id;
    private String name;
    private String exePath;
    private String command;
}
