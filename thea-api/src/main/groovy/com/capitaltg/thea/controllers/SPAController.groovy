package com.capitaltg.thea.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SPAController {

  private static final String CONFIG_PREFIX = 'thea_ui_'

  @GetMapping(value = '/**/{[path:[^\\.]*}')
  String redirect() {
    return 'forward:/'
  }

  @GetMapping(value = '/api/config')
  @ResponseBody
  Map getConfig() {
    return System.getenv()
      .findAll { it.key.toLowerCase().startsWith(CONFIG_PREFIX) }
      .collectEntries { k, v -> [k.toLowerCase()[CONFIG_PREFIX.length().value..-1], v] }
  }

}
