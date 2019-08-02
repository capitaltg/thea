package com.capitaltg.thea.util

import org.junit.Test

class CertificateUtilTest {

  @Test
  void getSubjectKeyIdentifier() {
    def certificate = CertificateUtil.loadCertificate('ctg.cer')
    def subjectKeyIdentifier = CertificateUtil.getSubjectKeyIdentifier(certificate)
    assert subjectKeyIdentifier == 'F49D3DABF1FA916E698799C9F31F99FAF916D931'
  }

  @Test
  void hexify() {
    byte[] array = 'hello'.bytes
    assert CertificateUtil.hexify(array) == '68656C6C6F'
  }

}
