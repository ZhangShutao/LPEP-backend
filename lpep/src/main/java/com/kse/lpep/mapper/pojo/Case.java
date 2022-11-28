package com.kse.lpep.mapper.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


import java.io.Serializable;
import java.io.File;



@Data
@Accessors(chain = true)
@TableName("t_case")
@NoArgsConstructor
public class Case implements Serializable {

    private static final long serialVersionUID = 2619918412167755103L;

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String progQuestionId;
    private Integer number;


    public String generateInputPath(String inputDir) {
        return inputDir + File.separator  + id + ".in";
    }

    public String generateOutputPath(String outputDir) {
        return outputDir + File.separator + id + ".out";
    }

}
