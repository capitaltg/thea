package com.capitaltg.thea.validators.chain;

import java.time.LocalDateTime;
import java.util.List;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.ChainValidator;

public class ExpiringCertificateInChainValidator implements ChainValidator {

  final private static int DAYS_WARNING = 90;

  @Override
  public boolean isInvalid(List<Certificate> certificates) {
    return certificates.subList(1, certificates.size()).stream()
        .anyMatch(certificate -> certificate.getNotAfter().isBefore(LocalDateTime.now().plusDays(DAYS_WARNING)));
  }

  @Override
  public String getMessage() {
    return "Intermediate CA certificate expires within " + DAYS_WARNING + " days";
  }

}
