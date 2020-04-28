package com.capitaltg.thea.repositories

import com.capitaltg.thea.objects.CertificateChain

import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CertificateChainRepository extends PagingAndSortingRepository <CertificateChain, Long>,
  JpaSpecificationExecutor<CertificateChain> {

  List<CertificateChain> findByHostname(String hostname)
  @Query(value='''
select distinct chain.hostname from
  CertificateChain chain
    join CertificateChain_Certificate ccc
    on chain.certificateChainId = ccc.CertificateChain_certificateChainId
  join Certificate certificate
    on certificate.certificateId = ccc.certificates_certificateId
where certificate.sha256 = :sha256
order by hostname
''', nativeQuery=true)
  List<String> findForCertificate(@Param('sha256') String sha256)

  @Query(value='''SELECT * FROM CertificateChain AS T1 WHERE hideResult=false and NOT EXISTS (
    SELECT * FROM CertificateChain AS T2 WHERE T2.hostname = T1.hostname AND T2.timestamp > T1.timestamp)
    order by timestamp desc limit 10''',
    nativeQuery=true)
  List<CertificateChain> findRecent()

  @Query(value='''
SELECT * FROM CertificateChain WHERE hostname = :hostname AND timestamp NOT IN
(SELECT max(timestamp) FROM CertificateChain WHERE hostname = :hostname)
ORDER BY timestamp DESC limit 10
''', nativeQuery=true)
  List<CertificateChain> findHistorical(@Param('hostname') String hostname)

}
