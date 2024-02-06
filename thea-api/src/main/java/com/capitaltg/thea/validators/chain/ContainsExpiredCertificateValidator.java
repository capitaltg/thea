package com.capitaltg.thea.validators.chain;

import java.util.List;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.ChainValidator;
import com.capitaltg.thea.validators.certificate.CertificateExpiredValidator;

public class ContainsExpiredCertificateValidator implements ChainValidator {

  @Override
  public boolean isInvalid(List<Certificate> certificates) {
    var validator = new CertificateExpiredValidator();
    return certificates.stream().anyMatch(validator::isInvalid);
  }

  @Override
  public String getMessage() {
    return "Chain contains expired certificate";
  }


}
