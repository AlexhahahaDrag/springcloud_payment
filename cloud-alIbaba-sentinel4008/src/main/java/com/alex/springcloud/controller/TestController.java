package com.alex.springcloud.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @GetMapping("/testA")
    public String testA() {
        try { TimeUnit.MILLISECONDS.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        return "test a";
    }

    @RequestMapping(value = "/testB", method = RequestMethod.GET)
    public String testB() {
        return "test b";
    }

    @RequestMapping(value = "/testC", method = RequestMethod.GET)
    public String tesC() {
        try { TimeUnit.MILLISECONDS.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
        return "test C";
    }

    @RequestMapping(value = "/testD", method = RequestMethod.GET)
    public String testD() {
        for (int i = 0; i <= 1; i++) {
            int a = 10 / i;
        }
        return "test D";
    }

    @RequestMapping(value = "/testE", method = RequestMethod.GET)
    public String tesE() {
        try { TimeUnit.MILLISECONDS.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        int a = 10/ 0;
        return "test C";
    }
}
