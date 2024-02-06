package com.capitaltg.thea.controllers;

import java.util.Collections;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SPAController {

  @GetMapping(value = "/^(?!\\/api).*$/")
  String redirect() {
    return "forward:/index.html";
  }

  @GetMapping(value = "/api/config")
  @ResponseBody
  Map<String, String> getConfig() {
    return Collections.emptyMap();
  }

  @RequestMapping("/api/user")
  @ResponseBody
  String getUser() {
    return null;
  }

  @GetMapping(value = "/api/logout")
  @ResponseBody
  void logout() {
  }

}
