package com.kse.lpep.controller;


import com.kse.lpep.controller.vo.exper.ExperStartRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exper")
public class ExperController {

    @PostMapping("/start")
    public void experStart(ExperStartRequest request){


    }
}
