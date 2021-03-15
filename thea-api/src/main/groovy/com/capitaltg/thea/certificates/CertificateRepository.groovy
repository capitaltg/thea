package com.capitaltg.thea.certificates

import com.capitaltg.thea.objects.Certificate

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface CertificateRepository extends JpaRepository<Certificate, Long>,
  JpaSpecificationExecutor<Certificate> {

  Certificate findBySha256(String sha256)
  List<Certificate> findBySubjectKeyIdentifier(String subjectKeyIdentifier)

  @Transactional
  @Modifying
  @Query(value = 'update Certificate set trusted = 0', nativeQuery = true)
  void untrustAll()

}
