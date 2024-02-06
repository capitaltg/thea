package com.capitaltg.thea.validators.certificate;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.CertificateValidator;

import java.time.LocalDateTime;

public class CertificateExpiredValidator implements CertificateValidator {
  
  @Override
  public boolean isInvalid(Certificate certificate) {
    boolean isExpired = certificate.getNotAfter().isBefore(LocalDateTime.now());
    return isExpired;
  }

  @Override
  public String getMessage() {
    return "Certificate is expired";
  }

}
