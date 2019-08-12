package com.capitaltg.thea.repositories

import com.capitaltg.thea.objects.User

import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends PagingAndSortingRepository <User, Long>,
JpaSpecificationExecutor<User> {

  Optional<User> findByEmail(String email)
  Boolean existsByEmail(String email)

}
