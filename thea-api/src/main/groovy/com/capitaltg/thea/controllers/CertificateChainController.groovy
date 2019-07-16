package com.capitaltg.thea.controllers

import com.capitaltg.thea.certificates.CertificateChainRepository
import com.capitaltg.thea.certificates.CertificateService
import com.capitaltg.thea.objects.CertificateChain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/api/chains')
class CertificateChainController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CertificateChainController)

  @Autowired
  CertificateService certificateService

  @Autowired
  CertificateChainRepository certificateChainRepository

  @GetMapping('/{id}')
  CertificateChain getCertificateChain(@PathVariable Long id) {
    LOGGER.info("Getting certificate chain $id")
    return certificateService.getCertificateChain(id)
  }

  @GetMapping('/recent')
  List<CertificateChain> getRecentCertificateChains() {
    LOGGER.info('Getting recent certificate chains')
    def recent = certificateChainRepository.findRecent()
    LOGGER.info("Found $recent.size recent chains")
    return recent
  }

  @GetMapping('/includes/{sha256}')
  List<CertificateChain> getCertificateChainsForCertificate(@PathVariable String sha256) {
    LOGGER.info("Getting certificate chains for $sha256")
    return certificateChainRepository.findForCertificate(sha256)
  }

  @PostMapping
  CertificateChain checkCertificateChain(@RequestParam String hostname,
      @RequestParam(required=false) boolean hideResult) {
    def trimmed = hostname.trim()
    LOGGER.info("Getting certificate chain for $trimmed")
    return certificateService.checkCertificateChain(trimmed, hideResult)
  }

  @GetMapping
  List<CertificateChain> searchCertificateChains(@RequestParam(required=false) String filters) {
    Map map = parseFilters(filters)
    def hostname = map.hostname
    LOGGER.info("Getting chains for $hostname")
    return certificateChainRepository.findByHostname(hostname)
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
