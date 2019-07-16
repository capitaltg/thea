package com.capitaltg.thea.validators.certificate

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.CertificateValidator

class Sha1SignatureValidator implements CertificateValidator {

  @Override
  boolean isInvalid(Certificate certificate) {
    return certificate.signatureAlgorithm == 'SHA1withRSA'
  }

  String message = 'Certificate uses weak SHA1 algorithm for signing'

}
