package com.kse.lpep.common.utility;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class ValidUtil {
//    public static String transListToString(List<String> errorMessage){
//        String res = "";
//        for(String s : errorMessage){
//            res = res + s + ";";
//        }
//        return res;
//    }

    // 校验失败时将错误信息组合成一个字符串
    public static String getValidErrorMessage(BindingResult bindingResult){
        List<String> errorMessageList = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        String res = "";
        for(String s : errorMessageList){
            res = res + s + ";";
        }
        return res;
    }
}
