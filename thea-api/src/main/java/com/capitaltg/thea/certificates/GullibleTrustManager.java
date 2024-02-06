package com.capitaltg.thea.certificates;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.X509TrustManager;

public class GullibleTrustManager implements X509TrustManager {

  List<X509Certificate> serverCertificates;

  @Override
  public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
    // do nothing
  }

  @Override
  public void checkServerTrusted(X509Certificate[] chain, String arg1) throws CertificateException {
    serverCertificates = Arrays.asList(chain);
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return null;
  }

  public List<X509Certificate> getServerCertificates() {
    return serverCertificates;
  }

}
