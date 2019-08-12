package com.capitaltg.thea.controllers

import com.capitaltg.thea.objects.User
import com.capitaltg.thea.services.UserService

import java.security.Principal

import javax.servlet.http.HttpServletRequest

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SPAController {

  private static final String CONFIG_PREFIX = 'thea_ui_'

  @Autowired
  UserService userService

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

  @RequestMapping('/api/user')
  @ResponseBody
  User getUser(Principal principal) {
    return userService.getUserByPrincipal(principal)
  }

  @GetMapping(value = '/api/logout')
  @ResponseBody
  void logout(HttpServletRequest request) {
    request.session.invalidate()
  }

}
