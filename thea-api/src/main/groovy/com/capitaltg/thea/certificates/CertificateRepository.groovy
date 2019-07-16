package com.capitaltg.thea.certificates

import com.capitaltg.thea.objects.Certificate

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface CertificateRepository extends JpaRepository<Certificate, Long>,
  JpaSpecificationExecutor<Certificate> {

  Certificate findBySha256(String sha256)
  List<Certificate> findBySubjectKeyIdentifier(String subjectKeyIdentifier)

}
