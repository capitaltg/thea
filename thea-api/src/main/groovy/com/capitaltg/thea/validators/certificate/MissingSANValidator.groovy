package com.capitaltg.thea.validators.certificate

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.CertificateValidator

class MissingSANValidator implements CertificateValidator {

  @Override
  boolean isInvalid(Certificate certificate) {
    !certificate.certificateAuthority && !certificate.subjectAlternativeNames
  }

  String message = 'Certificate does not contain any subject alternative names'

}
