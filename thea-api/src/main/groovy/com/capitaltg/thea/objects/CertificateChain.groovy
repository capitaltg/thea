package com.capitaltg.thea.objects

import com.capitaltg.thea.util.ListSerializer

import java.time.ZonedDateTime

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Entity
class CertificateChain {

  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long certificateChainId
  ZonedDateTime timestamp = ZonedDateTime.now()
  String hostname

  @Column(length = 4000)
  @Convert(converter = ListSerializer)
  List warnings = []

  boolean success
  boolean hideResult = false

  @JoinTable
  @ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
  List<Certificate> certificates

}
