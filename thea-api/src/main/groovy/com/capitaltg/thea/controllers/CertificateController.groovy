package com.capitaltg.thea.controllers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import com.capitaltg.thea.certificates.CertificateService
import com.capitaltg.thea.objects.Certificate

import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping('/api/certificates')
class CertificateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CertificateController)

  @Autowired
  CertificateService certificateService

  @GetMapping
  List<Certificate> searchCertificates(@RequestParam(required=false) String filters) {
    LOGGER.info("Searching for: $filters")
    Map map = parseFilters(filters)
    return certificateService.searchCertificates(map)
  }

  @GetMapping('/{sha256}')
  Certificate getCertificate(@PathVariable String sha256) {
    LOGGER.info("Getting certificate $sha256")
    return certificateService.getCertificate(sha256)
  }

  @GetMapping('/trusted')
  List<Certificate> getTrustedCertificates() {
    LOGGER.info('Getting trusted certificates')
    return certificateService.getTrustedAnchorCertificates()
  }

  @GetMapping('/{sha256}/raw')
  def downloadCertificate(@PathVariable String sha256, HttpServletResponse response) {
    LOGGER.info("Getting certificate $sha256")
    def certificate = certificateService.getCertificate(sha256)
    HttpHeaders headers = new HttpHeaders()
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM)
    response.setHeader('Content-Disposition', """attachment; filename= "${certificate.subjectDn}.cer" """)
    def string = """-----BEGIN CERTIFICATE-----
${certificate.base64Encoding}
-----END CERTIFICATE-----"""
    return string
  }

  @GetMapping('/{sha256}/chain')
  def downloadChain(@PathVariable String sha256, HttpServletResponse response) {
    LOGGER.info("Getting certificate chain for $sha256")
    def certificate = certificateService.getCertificate(sha256)
    def certificates = certificateService.getCertificateChain(sha256)
    def string = certificates.collect {
      """-----BEGIN CERTIFICATE-----
${it.base64Encoding}
-----END CERTIFICATE-----"""
    }.join('\n')
    HttpHeaders headers = new HttpHeaders()
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM)
    response.setHeader('Content-Disposition', """attachment; filename= "${certificate.commonName}-chain.cer" """)
    return string
  }

  @GetMapping('/{sha256}/similar')
  def getSimilarCertificates(@PathVariable String sha256) {
    LOGGER.info("Getting certificate $sha256")
    def certificate = certificateService.getCertificate(sha256)
    return certificateService.getSimilarCertificates(certificate.subjectKeyIdentifier, sha256)
  }

  Map parseFilters(String filters) {
    if (!filters) {
      return [:]
    }
    def map = [:]
    filters.split(',').each { map[it.split(':')[0]] = it.split(':')[1] }
    return map
  }

}
