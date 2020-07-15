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
select
  max(chain.certificateChainId) as certificateChainId,
  chain.hostname as hostname,
  max(chain.timestamp) as timestamp
from CertificateChain as chain
join chain.certificates as certificates
where
  certificates.sha256 = :sha256 and
  chain.hideResult = false
group by chain.hostname
''')
  List<CertificateChain> findForCertificate(@Param('sha256') String sha256)

  @Query(value='''SELECT * FROM CertificateChain AS T1 WHERE hideResult=false and NOT EXISTS (
    SELECT * FROM CertificateChain AS T2 WHERE T2.hostname = T1.hostname AND T2.timestamp > T1.timestamp)
    order by timestamp desc limit 10''',
    nativeQuery=true)
  List<CertificateChain> findRecent()

  @Query(value='''
SELECT * FROM CertificateChain
WHERE
  hostname = :hostname and
  hideResult = false
ORDER BY timestamp DESC limit 10
''', nativeQuery=true)
  List<CertificateChain> findHistorical(@Param('hostname') String hostname)

}
