package com.capitaltg.thea.validators.chain;

import java.util.List;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.ChainValidator;

/**
 * Validates that each authority key identifier matches its
 * issuer's subject key identifier
 */
public class MatchingAuthorityValidator implements ChainValidator {

  @Override
  public boolean isInvalid(List<Certificate> certificates) {
    var authorityKeyIdentifier = certificates.get(0).getAuthorityKeyIdentifier();
    var valid = false;
    for (int i = 1; i < certificates.size(); i++) {
      if (!certificates.get(i).getSubjectKeyIdentifier().equals(authorityKeyIdentifier)) {
        valid = true;
      }
      authorityKeyIdentifier = certificates.get(i).getAuthorityKeyIdentifier();
    }
    return valid;
  }
  @Override
  public String getMessage() {
    return "Invalid certificate chain; mismatched subject and authority key identifiers";
  }

}
