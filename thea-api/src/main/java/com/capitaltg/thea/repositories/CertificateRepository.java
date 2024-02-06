package com.capitaltg.thea.repositories;

import com.capitaltg.thea.entities.Certificate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long>,
    JpaSpecificationExecutor<Certificate> {

  public Certificate findBySha256(String sha256);

  public List<Certificate> findBySubjectKeyIdentifier(String subjectKeyIdentifier);

  @Transactional
  @Modifying
  @Query("update Certificate c set c.trusted = false")
  public void untrustAll();

}
