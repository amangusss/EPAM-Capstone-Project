package com.github.amanguss.shopping_list_application.controller.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class WebController {

    @GetMapping("/health")
    public String health(Model model) {
        return "OK";
    }
}