package com.capitaltg.thea.config

import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository

@EnableOAuth2Sso
@Configuration
class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .antMatcher('/**')
      .authorizeRequests()
        .antMatchers('/api/user').authenticated()
        .antMatchers('/api/**', '/login**', '/error**').permitAll()
      .anyRequest()
        .authenticated()
      .and().logout().logoutSuccessUrl('/').permitAll()
      .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
  }

}
