package com.capitaltg.thea.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class SPAController {

  @GetMapping(value = '/**/{[path:[^\\.]*}')
  String redirect() {
    return 'forward:/'
  }

}
