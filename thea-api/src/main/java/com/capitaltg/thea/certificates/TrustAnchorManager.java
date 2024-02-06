package com.capitaltg.thea.certificates;

import com.capitaltg.thea.util.CertificateUtil;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrustAnchorManager {

  private static final TrustAnchorManager TRUST_ANCHOR_MANAGER = new TrustAnchorManager();
  public List<String> subjectKeyIdentifiers;

  public static TrustAnchorManager singleton() {
    return TRUST_ANCHOR_MANAGER;
  }

  TrustAnchorManager() {
    try {
    subjectKeyIdentifiers = getAllTrustedAnchors()
      .stream()
      .map(certificate -> CertificateUtil.getSubjectKeyIdentifier(certificate))
      .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("Failed to initialize", e);
    }
  }

  public boolean isTrustedAnchor(String subjectKeyIdentifier) {
    return subjectKeyIdentifiers.contains(subjectKeyIdentifier);
  }

  public List<X509Certificate> getAllTrustedAnchors() throws NoSuchAlgorithmException, KeyStoreException {
    TrustManagerFactory trustManagerFactory =
      TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init((KeyStore) null);

    X509TrustManager defaultTrustManager = (X509TrustManager)Arrays.asList(trustManagerFactory.getTrustManagers())
      .stream()
      .filter(manager -> manager instanceof X509TrustManager)
      .findFirst()
      .get();
      
    return Arrays.asList(defaultTrustManager.getAcceptedIssuers());
  }

}
