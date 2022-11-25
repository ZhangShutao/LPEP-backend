package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.Serializable;
import java.util.StringJoiner;

@Data
@Accessors(chain = true)
@TableName("t_case")
public class Case implements Serializable {
    private static final long serialVersionUID = 1126406801036107228L;
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String progQuestionId;
    private Integer number;

    public String generateInputPath(String inputDir) {
        return inputDir + File.pathSeparator  + id + ".in";
    }

    public String generateOutputPath(String outputDir) {
        return outputDir + File.pathSeparator + id + ".out";
    }
}
