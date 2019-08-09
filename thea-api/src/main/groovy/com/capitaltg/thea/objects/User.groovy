package com.capitaltg.thea.objects

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

import org.hibernate.annotations.NaturalId

@Entity
class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id

  @NaturalId
  @Column(length = 50, unique = true, nullable = false)
  String email
  String name

}
