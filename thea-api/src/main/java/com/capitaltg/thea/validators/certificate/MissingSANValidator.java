package com.capitaltg.thea.validators.certificate;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.CertificateValidator;

public class MissingSANValidator implements CertificateValidator {

  @Override
  public boolean isInvalid(Certificate certificate) {    
    return !certificate.isCertificateAuthority() && certificate.getSubjectAlternativeNames().isEmpty();
  }

  @Override
  public String getMessage() {
    return "Certificate does not contain any subject alternative names";
  }

}
