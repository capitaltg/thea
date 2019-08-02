package com.capitaltg.thea.objects

import org.junit.Test

import com.capitaltg.thea.util.CertificateUtil

class CertificateTest {

  @Test
  void testKeys() {
    def certificate = new Certificate(CertificateUtil.loadCertificate('google.cer'))
    assert certificate.keyType == 'ECPublicKey'
    assert certificate.keyLength == 360
  }

}
