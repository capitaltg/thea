package com.capitaltg.thea.entities;

import com.capitaltg.thea.util.ListSerializer;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "CertificateChain")
public class CertificateChain {

  @Id
  @Column(unique = true, nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long certificateChainId;
  ZonedDateTime timestamp = ZonedDateTime.now();
  String hostname;

  @Column(length = 4000)
  @Convert(converter = ListSerializer.class)
  List<String> warnings = new ArrayList<>();

  boolean success;
  boolean hideResult = false;

  @JoinTable
  @ManyToMany(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
  List<Certificate> certificates;

}
