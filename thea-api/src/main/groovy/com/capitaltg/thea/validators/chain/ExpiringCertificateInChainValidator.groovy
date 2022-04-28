package com.capitaltg.thea.validators.chain

import java.time.LocalDateTime

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.ChainValidator

class ExpiringCertificateInChainValidator implements ChainValidator {

  final static int DAYS_WARNING = 90

  @Override
  boolean isInvalid(List<Certificate> certificates) {
    certificates.subList(1, certificates.size()).find { certificate ->
      certificate.notAfter.isBefore(LocalDateTime.now().plusDays(DAYS_WARNING))
    }
  }

  String message = "Intermediate CA certificate expires within $DAYS_WARNING days"

}
