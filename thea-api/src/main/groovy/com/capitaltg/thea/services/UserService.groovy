package com.capitaltg.thea.services

import java.security.Principal

import javax.transaction.Transactional

import com.capitaltg.thea.objects.User
import com.capitaltg.thea.repositories.UserRepository

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService)

  @Autowired
  UserRepository userRepository

  @Transactional
  User getUserByPrincipal(Principal principal) {
    def email = principal?.userAuthentication?.details?.email
    def optional = userRepository.findByEmail(email)
    if (optional.isPresent()) {
      LOGGER.debug("Found user for $email")
      return optional.get()
    }
    def name = principal?.userAuthentication?.details?.name
    def user = new User(email: email, name: name)
    userRepository.save(user)
    LOGGER.info("Created user for $email")
    user = userRepository.findByEmail(email).get()
    return user
  }

}
