package com.capitaltg.thea.services

import com.capitaltg.thea.certificates.CertificateChainValidator
import com.capitaltg.thea.certificates.CertificateRepository
import com.capitaltg.thea.certificates.GullibleTrustManager
import com.capitaltg.thea.certificates.TrustAnchorManager
import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.objects.CertificateChain
import com.capitaltg.thea.repositories.CertificateChainRepository
import com.google.common.base.Preconditions

import java.security.SecureRandom
import java.security.Security
import java.security.cert.CertPath
import java.security.cert.CertPathBuilder
import java.security.cert.CertPathValidator
import java.security.cert.CertPathValidatorException
import java.security.cert.CertificateException

import java.security.cert.CertificateFactory
import java.security.cert.CertificateRevokedException
import java.security.cert.PKIXBuilderParameters
import java.security.cert.PKIXRevocationChecker
import java.security.cert.TrustAnchor
import java.security.cert.X509CertSelector
import java.time.LocalDateTime

import javax.annotation.PostConstruct

import javax.net.ssl.CertPathTrustManagerParameters
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

import org.hibernate.SessionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.apache.commons.lang3.StringUtils

@Service
class CertificateService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CertificateService)

  @Autowired
  CertificateRepository certificateRepository

  @Autowired
  CertificateChainRepository certificateChainRepository

  @Autowired
  SessionFactory sessionFactory

  @Autowired
  CertificateChainValidator validator

  TrustAnchorManager trustAnchorManager = TrustAnchorManager.singleton()

  CertificateService() {
    System.setProperty('com.sun.net.ssl.checkRevocation', 'true')
    Security.setProperty('ocsp.enable', 'true')
  }

  CertificateChain checkCertificateChain(def hostname, def hideResult = false) {
    CertPathBuilder certPathBuilder = CertPathBuilder.getInstance('PKIX')
    def revocationChecker = (PKIXRevocationChecker)certPathBuilder.getRevocationChecker()
    revocationChecker.setOptions(EnumSet.of(
      PKIXRevocationChecker.Option.PREFER_CRLS,
      PKIXRevocationChecker.Option.ONLY_END_ENTITY))
    Set<TrustAnchor> anchors = trustAnchorManager.allTrustedAnchors.collect { new TrustAnchor(it, null) }
    PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(anchors, new X509CertSelector())
    pkixParams.addCertPathChecker(revocationChecker)
    pkixParams.setRevocationEnabled(true)

    def gullibleTrustManager = new GullibleTrustManager()
    def tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    tmf.init( new CertPathTrustManagerParameters(pkixParams))
    def defaultTrustManager = tmf.trustManagers.find { it instanceof X509TrustManager }

    SSLContext sslContext = SSLContext.getInstance('TLS')
    sslContext.init(
      null,
      [gullibleTrustManager, defaultTrustManager] as TrustManager[],
      new SecureRandom())
    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory()
    URL url = new URL("https://${hostname}")
    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection()
    connection.connectTimeout = 5000
    connection.setSSLSocketFactory(sslSocketFactory)
    def warnings = []
    try {
      connection.connect()
      CertPathValidator validator = CertPathValidator.getInstance('PKIX')
      CertPath certPath = CertificateFactory.getInstance('X.509')
        .generateCertPath(gullibleTrustManager.serverCertificates)
      validator.validate(certPath, pkixParams)
    } catch (CertificateException | SSLHandshakeException e) {
      if (e.message.contains('CertificateException')) {
        warnings.add(StringUtils.substringAfterLast(e.message, 'CertificateException: '))
      } else if (e.message.contains('unrecognized_name')) {
        warnings.add "Server is not configured to respond to requests to $hostname" as String
      } else {
        LOGGER.error("Failed to connect to $hostname", e)
        warnings.add(e.message)
      }
    } catch (SocketTimeoutException e) {
      warnings.add 'Failed to connect due to timeout'
    } catch (CertPathValidatorException e) {
      if (e.cause instanceof CertificateRevokedException) {
        warnings.add e.cause.message
      } else if (e.message == 'Unable to determine revocation status due to network error') {
        warnings.add 'Unable to determine revocation status due to network error'
      } else {
        LOGGER.error("Failed to validate $hostname", e)
      }
    } catch (ConnectException e) {
      LOGGER.warn("Unable to connect to $hostname")
      warnings.add("Unable to connect to $hostname" as String)
    } catch (UnknownHostException e) {
      warnings.add("$hostname is an unknown host" as String)
    } catch (Exception e) {
      LOGGER.error "Failed to connect to $hostname", e
    }

    def chain = new CertificateChain()
    chain.hostname = hostname
    chain.certificates = gullibleTrustManager.serverCertificates.collect { new Certificate(it) }
    if (chain.certificates) {
      validator.validateCertificateChain(chain)
      chain.warnings = chain.warnings + warnings
      chain.success = (chain.warnings + chain.certificates*.warnings.flatten()).size() == 0
      chain.hideResult = hideResult
      saveThem(chain)
    } else if (warnings) {
      chain.warnings = warnings
    } else {
      chain.warnings = ['Server does not support TLS connection']
    }
    return chain
  }

  CertificateChain getCertificateChainById(long id) {
    return certificateChainRepository.findById(id).orElse(null)
  }

  Certificate getCertificate(String sha256) {
    return certificateRepository.findBySha256(sha256)
  }

  List<Certificate> getCertificateChain(String sha256) {
    // TODO can probably use some cleanup
    def certificate = certificateRepository.findBySha256(sha256)
    Preconditions.checkNotNull(certificate, "Certificate with SHA256 hash $sha256 does not exist")

    def listOfChains = [[ certificate ]]
    def addedCerts = true

    // build all possible, unexpired chains
    while (addedCerts) {
      addedCerts = false
      def newListOfChains = []
      listOfChains.each { chain ->
        def lastCertificate = chain.last()
        if (lastCertificate.authorityKeyIdentifier &&
          lastCertificate.authorityKeyIdentifier != lastCertificate.subjectKeyIdentifier) {
          def authorities = certificateRepository.findBySubjectKeyIdentifier(lastCertificate.authorityKeyIdentifier)
          authorities
            // remove expired certificates from consideration
            .findAll { it.notAfter > LocalDateTime.now() }
            .each { authority ->
              newListOfChains.push(chain + authority)
              addedCerts = true
            }
          } else {
          newListOfChains.push(chain)
          }
      }
      listOfChains = newListOfChains
    }

    // sort by length and return the longest one
    listOfChains.sort { c1, c2 -> c1.size() <=> c2.size() }
    def chain = listOfChains.last()
    chain.remove(0)

    // remove the first certificate (original) and last (if a trust anchor)
    def last = chain.size()
    if (last == 0) {
      return chain
    }
    def lastCertificate = chain.last()
    if (!lastCertificate.authorityKeyIdentifier ||
          lastCertificate.authorityKeyIdentifier == lastCertificate.subjectKeyIdentifier) {
      last = last - 1
    }
    return chain.subList(0, last)
  }

  List<Certificate> getSimilarCertificates(String sha256) {
    def certificate = getCertificate(sha256)
    if (!certificate || !certificate.subjectKeyIdentifier) {
      return []
    }
    def list = certificateRepository.findBySubjectKeyIdentifier(certificate.subjectKeyIdentifier)
    return list.findAll { it.sha256 != sha256 }
  }

  void saveThem(CertificateChain chain) {
    def session = sessionFactory.openSession()
    try {
      session.beginTransaction()
      chain.certificates = chain.certificates.collect {
        def existing = session.bySimpleNaturalId(Certificate).load(it.sha256)
        if (!existing) {
          session.saveOrUpdate(it)
          existing = session.bySimpleNaturalId(Certificate).load(it.sha256)
        }
        return existing
      }
      session.saveOrUpdate(chain)
      session.getTransaction().commit()
    } finally {
      session.close()
    }
  }

  List<Certificate> searchCertificates(Map map) {
    def sql = '''from Certificate order by commonName'''
    def session = sessionFactory.openSession()

    def clauses = map.collect { k, v ->
      if (k.toString().toLowerCase() == 'subjectdn') {
        "( upper($k) like :$k OR upper(subjectAlternativeNames) like cast(:$k as string) )"
      } else {
        "upper($k) like :$k"
      }
    }
    def whereClause = clauses.join(' AND ')
    if (clauses) {
      sql = "from Certificate where $whereClause order by commonName"
    }

    try {
      def query = session.createQuery(sql)
      map.each { k, v -> query.setParameter(k, "%${v.toUpperCase()}%" as String) }
      query.setMaxResults(20).list()
    } finally {
      session.close()
    }
  }

  List<Certificate> getTrustedAnchorCertificates() {
    def list = trustAnchorManager.allTrustedAnchors.collect { new Certificate(it) }
    return list.sort { c1, c2 -> c1.subjectDn <=> c2.subjectDn }
  }

  void saveCertificate(Certificate certificate) {
    def session = sessionFactory.openSession()
    try {
      def existing = session.bySimpleNaturalId(Certificate).load(certificate.sha256)
      if (!existing) {
        session.save(certificate)
      }
    } finally {
      session.close()
    }
  }

  @PostConstruct
  def storeTrustAnchorCertificates() {
    def session = sessionFactory.openSession()
    try {
      // TODO untrust all and re-establish trusted certs
      trustAnchorManager.allTrustedAnchors.each {
        def certificate = new Certificate(it)
        certificate.trusted = true
        def existing = session.bySimpleNaturalId(Certificate).load(certificate.sha256)
        if (!existing) {
          session.save(certificate)
        }
      }
    } finally {
      session.close()
    }
  }

}
