package com.capitaltg.thea.validators.chain

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.ChainValidator
import com.capitaltg.thea.validators.certificate.CertificateExpiredValidator

class ContainsExpiredCertificateValidator implements ChainValidator {

  @Override
  boolean isInvalid(List<Certificate> certificates) {
    def validator = new CertificateExpiredValidator()
    return certificates.find { validator.isInvalid(it) }
  }

  String message = 'Chain contains expired certificate'

}
