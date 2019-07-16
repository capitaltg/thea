package com.capitaltg.thea.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class CorsConfiguration  implements WebMvcConfigurer {

  @Override
  void addCorsMappings(CorsRegistry registry) {
    registry.addMapping('/**')
      .allowedOrigins('*')
      .allowedMethods('GET', 'POST', 'PUT', 'DELETE', 'HEAD')
      .allowCredentials(true)
  }

}
