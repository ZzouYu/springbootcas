package com.cas.casseckill.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author zouyu
 * @description
 * @date 2020/4/30
 */
@Controller
public class LoginController {
    @Value("${spring.cas.casServerUrl}")
    private String casServerUrl;
    @Value("${spring.cas.casClientUrl}")
    private String clientHostUrl;
    @Value("${server.port}")
    private String port;

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:http://" + casServerUrl + "/cas/logout?service=http://" + clientHostUrl + ":" + port
                + "/logoutSucess";
    }
    @GetMapping("/index")
    @ResponseBody
    public String index() {
        return "所属";
    }
    /**
     * 退出成功页
     * @return
     */
    @RequestMapping("/logoutSucess")
    @ResponseBody
    public String logoutSuccess() {
        return "logoutSuccess";
    }
}
