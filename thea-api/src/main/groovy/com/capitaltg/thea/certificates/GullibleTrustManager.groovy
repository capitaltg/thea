package com.capitaltg.thea.certificates

import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import javax.net.ssl.X509TrustManager

class GullibleTrustManager implements X509TrustManager {

  List<X509Certificate> serverCertificates

  @Override
  void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    // do nothing
  }

  @Override
  void checkServerTrusted(X509Certificate[] chain, String arg1) throws CertificateException {
    serverCertificates = chain as List
  }

  @Override
  X509Certificate[] getAcceptedIssuers() {
    return null
  }

  List<X509Certificate> getServerCertificates() {
    return serverCertificates
  }

}
