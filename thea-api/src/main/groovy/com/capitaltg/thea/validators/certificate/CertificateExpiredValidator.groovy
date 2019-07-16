package com.capitaltg.thea.validators.certificate

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.CertificateValidator

import java.time.LocalDateTime

class CertificateExpiredValidator implements CertificateValidator {

  @Override
  boolean isInvalid(Certificate certificate) {
    certificate.notAfter < LocalDateTime.now()
  }

  String message = 'Certificate is expired'

}
