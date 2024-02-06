package com.capitaltg.thea.validators.chain;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.ChainValidator;

import java.util.List;

import com.capitaltg.thea.certificates.TrustAnchorManager;

public class TrustedChainValidator implements ChainValidator {

  private List<String> subjectKeyIdentifiers = TrustAnchorManager.singleton().subjectKeyIdentifiers;

  @Override
  public boolean isInvalid(List<Certificate> certificates) {
    return !certificates.stream().anyMatch(cert -> subjectKeyIdentifiers.contains(cert.getAuthorityKeyIdentifier()));
  }

  @Override
  public String getMessage() {
    return "Chain is not anchored by trusted certificate authority";
  }

}
