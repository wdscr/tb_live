package com.example.tb_live_catch;

import com.example.tb_live_catch.service.FfmpegService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    FfmpegService ffmpegService;

    @GetMapping("/test")
    public String test() {
        return "success";
    }

}
