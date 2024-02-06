package com.capitaltg.thea.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.capitaltg.thea.entities.CertificateChain;

@Repository
public interface CertificateChainRepository extends PagingAndSortingRepository<CertificateChain, Long>,
    JpaSpecificationExecutor<CertificateChain> {

  public List<CertificateChain> findByHostname(String hostname);

  public Optional<CertificateChain> findById(long id);

  @Query(value = """
            SELECT c FROM CertificateChain c
            WHERE c.hideResult = false
            AND NOT EXISTS (
              SELECT 1 FROM CertificateChain cc
              WHERE cc.hostname = c.hostname
              AND cc.timestamp > c.timestamp
              AND cc.hideResult = false
            )
            ORDER BY c.timestamp DESC
            limit 10
      """)
  public List<CertificateChain> findRecent();

  @Query(value = """
        SELECT c FROM CertificateChain c
        JOIN c.certificates cert
        WHERE
          cert.sha256 = :sha256 AND
          c.hideResult = false
          AND NOT EXISTS (
            SELECT 1 FROM CertificateChain cc
            WHERE cc.hostname = c.hostname
            AND cc.timestamp > c.timestamp
            AND cc.hideResult = false
          )
          ORDER BY c.timestamp DESC
        LIMIT 20
        """)
  public List<CertificateChain> findForCertificate(@Param("sha256") String sha256);
  
  @Query(value = """
      SELECT c FROM CertificateChain c
      WHERE
        c.hostname = :hostname and
        c.hideResult = false
        ORDER BY timestamp DESC limit 10
      """)
  public List<CertificateChain> findHistorical(@Param("hostname") String hostname);

}
