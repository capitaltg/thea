package com.capitaltg.thea.validators.certificate

import com.capitaltg.thea.certificates.TrustAnchorManager
import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.CertificateValidator

class SelfSignedTrustAnchorValidator implements CertificateValidator {

  @Override
  boolean isInvalid(Certificate certificate) {
    return (certificate.subjectDn == certificate.issuerDn || !certificate.authorityKeyIdentifier) &&
      TrustAnchorManager.singleton().isTrustedAnchor(certificate.subjectKeyIdentifier)
  }

  String message = 'Certificate is a self-signed trust anchor'

}
