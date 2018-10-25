package com.tmw.etl.etlapp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EtlController {

    @GetMapping("/")
    public String homePage(){
        return "index";
    }

    @GetMapping("/extract")
    public String extract(){
        return "extract";
    }

    @GetMapping("/transfer")
    @ResponseBody
    public String transfer(){
        return "transfer";
    }

    @GetMapping("/load")
    @ResponseBody
    public String load(){
        return "load";
    }
}
