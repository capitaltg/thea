package com.capitaltg.thea.validators.chain;

import java.util.List;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.ChainValidator;
import com.capitaltg.thea.validators.certificate.SelfSignedTrustAnchorValidator;

public class ContainsSelfSignedValidator implements ChainValidator {

  @Override
  public boolean isInvalid(List<Certificate> certificates) {
    var validator = new SelfSignedTrustAnchorValidator();
    return certificates.stream().anyMatch(validator::isInvalid);
  }

  @Override
  public String getMessage() {
    return "Chain contains self-signed trust anchor certificate";
  }

}
