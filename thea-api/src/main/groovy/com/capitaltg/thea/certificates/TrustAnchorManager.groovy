package com.capitaltg.thea.certificates

import com.capitaltg.thea.util.CertificateUtil

import java.security.KeyStore
import java.security.cert.X509Certificate

import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

import org.springframework.stereotype.Component

@Component
class TrustAnchorManager {

  private static final TrustAnchorManager TRUST_ANCHOR_MANAGER = new TrustAnchorManager()
  List subjectKeyIdentifiers

  static TrustAnchorManager singleton() {
    return TRUST_ANCHOR_MANAGER
  }

  TrustAnchorManager() {
    subjectKeyIdentifiers = getAllTrustedAnchors()
      .collect { CertificateUtil.getSubjectKeyIdentifier(it) }
      .findAll { it }
  }

  boolean isTrustedAnchor(String subjectKeyIdentifier) {
    return subjectKeyIdentifier in subjectKeyIdentifiers
  }

  List<X509Certificate> getAllTrustedAnchors() {
    TrustManagerFactory trustManagerFactory =
      TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    trustManagerFactory.init((KeyStore) null)

    X509TrustManager defaultTrustManager = trustManagerFactory
      .getTrustManagers().find { it instanceof X509TrustManager }
    return defaultTrustManager.acceptedIssuers
  }

}
