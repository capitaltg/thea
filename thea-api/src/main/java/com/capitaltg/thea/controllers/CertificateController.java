package com.capitaltg.thea.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.services.CertificateService;

import jakarta.servlet.http.HttpServletResponse;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CertificateController.class);

  @Autowired
  CertificateService certificateService;

  @GetMapping
  public List<Certificate> searchCertificates(@RequestParam(required=false) String filters) {
    LOGGER.info("Searching for: " + filters);
    Map<String, String> map = parseFilters(filters);
    return certificateService.searchCertificates(map);
  }

  @GetMapping("/{sha256}")
  public Certificate getCertificate(@PathVariable String sha256) {
    LOGGER.info("Getting certificate " + sha256);
    return certificateService.getCertificate(sha256);
  }

  @GetMapping("/trusted")
  public List<Certificate> getTrustedCertificates() throws NoSuchAlgorithmException, KeyStoreException {
    LOGGER.info("Getting trusted certificates");
    return certificateService.getTrustedAnchorCertificates();
  }

  @GetMapping("/{sha256}/raw")
  public String downloadCertificate(@PathVariable String sha256, HttpServletResponse response) {
    LOGGER.info("Getting certificate " + sha256);
    Certificate certificate = certificateService.getCertificate(sha256);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    response.setHeader("Content-Disposition", "attachment; filename=\"" + certificate.getCommonName() + ".cer\"");
    String string = "-----BEGIN CERTIFICATE-----\n" + certificate.getBase64Encoding() + "\n-----END CERTIFICATE-----";
    return string;
  }

  @GetMapping("/{sha256}/chain")
  public String downloadChain(@PathVariable String sha256, HttpServletResponse response) throws NoSuchAlgorithmException, KeyStoreException {
    LOGGER.info("Getting certificate chain for " + sha256);
    Certificate certificate = certificateService.getCertificate(sha256);
    List<Certificate> certificates = certificateService.getCertificateChain(sha256);
    String string = certificates.stream()
            .map(cert -> "-----BEGIN CERTIFICATE-----\n" + cert.getBase64Encoding() + "\n-----END CERTIFICATE-----")
            .collect(Collectors.joining("\n"));
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
    response.setHeader("Content-Disposition", "attachment; filename=\"" + certificate.getCommonName() + "-chain.cer\"");
    return string;
  }

  @GetMapping("/{sha256}/similar")
  public List<Certificate> getSimilarCertificates(@PathVariable String sha256) {
    LOGGER.info("Getting certificate " + sha256);
    return certificateService.getSimilarCertificates(sha256);
  }

  private Map<String, String> parseFilters(String filters) {
    if (filters == null || filters.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, String> map = new HashMap<>();
    String[] filterArray = filters.split(",");
    for (String filter : filterArray) {
      String[] keyValue = filter.split(":");
      map.put(keyValue[0], keyValue[1]);
    }
    return map;
  }

}
