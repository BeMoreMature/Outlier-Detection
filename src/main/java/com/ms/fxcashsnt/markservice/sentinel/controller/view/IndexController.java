package com.ms.fxcashsnt.markservice.sentinel.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * user: yandongl
 * date: 8/28/2018
 */
@Controller
public class IndexController {

    @RequestMapping(value = "/sentinel")
    public String index() {
        return "index.html";
    }
}
