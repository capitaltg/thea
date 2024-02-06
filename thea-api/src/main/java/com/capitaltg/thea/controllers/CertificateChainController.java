package com.capitaltg.thea.controllers;

import com.capitaltg.thea.entities.CertificateChain;
import com.capitaltg.thea.repositories.CertificateChainRepository;
import com.capitaltg.thea.services.CertificateService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/chains")
public class CertificateChainController {

  @Autowired
  CertificateService certificateService;

  @Autowired
  CertificateChainRepository certificateChainRepository;

  @GetMapping("/{id}")
  public CertificateChain getCertificateChain(@PathVariable Long id) {
    log.info("Getting certificate chain {}", id);
    return certificateService.getCertificateChainById(id);
  }

  @GetMapping("/historical/{hostname}")
  List<CertificateChain> getCertificateChain(@PathVariable String hostname) {
    var historicalChains = certificateChainRepository.findHistorical(hostname);
    log.info("Found {} historical chains for {}", historicalChains.size(), hostname);
    return historicalChains;
  }

  @GetMapping("/recent")
  List<CertificateChain> getRecentCertificateChains() {
    var recent = certificateChainRepository.findRecent();
    log.info("Found {} recent chains", recent.size());
    return recent;
  }

  @GetMapping("/includes/{sha256}")
  public List<CertificateChain> getCertificateChainsForCertificate(@PathVariable String sha256) {
    log.info("Getting certificate chains for " + sha256);
    return certificateChainRepository.findForCertificate(sha256);
  }

  @PostMapping
  CertificateChain checkCertificateChain(@RequestParam String hostname,
      @RequestParam(required=false) boolean hideResult) {
    var trimmed = hostname.trim();
    log.info("Getting certificate chain for {}", trimmed);
    return certificateService.checkCertificateChain(trimmed, hideResult);
  }

  @GetMapping
  List<CertificateChain> searchCertificateChains(@RequestParam(required=false) String hostname) {
    log.info("Getting chains for $hostname");
    return certificateChainRepository.findByHostname(hostname);
  }

}
