package com.capitaltg.thea.validators.certificate

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.util.CertificateUtil

import org.junit.Test

class SelfSignedTrustAnchorValidatorTest {

  @Test
  void valid() {
    def validator = new SelfSignedTrustAnchorValidator()
    def certificate = new Certificate(CertificateUtil.loadCertificate('ctg.cer'))
    assert validator.isInvalid(certificate) == false
  }

  @Test
  void invalid() {
    def validator = new SelfSignedTrustAnchorValidator()
    def certificate = new Certificate(CertificateUtil.loadCertificate('baltimore.cer'))
    assert validator.isInvalid(certificate) == true
    assert validator.message == 'Certificate is a self-signed trust anchor'
  }

}
