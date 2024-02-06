package com.capitaltg.thea.validators.certificate;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.CertificateValidator;

public class Sha1SignatureValidator implements CertificateValidator {

  @Override
  public boolean isInvalid(Certificate certificate) {
    return certificate.getSignatureAlgorithm().equals("SHA1withRSA");
  }

  @Override
  public String getMessage() {
    return "Certificate uses weak SHA1 algorithm for signing";
  }

  
}
