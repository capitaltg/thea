package com.capitaltg.thea.validators.chain

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.ChainValidator

/**
 * Validates that each authority key identifier matches its
 * issuer's subject key identifier
 */
class MatchingAuthorityValidator implements ChainValidator {

  @Override
  boolean isInvalid(List<Certificate> certificates) {
    def authorityKeyIdentifier = certificates.first().authorityKeyIdentifier
    def valid = false
    certificates.subList(1, certificates.size()).each {
      if (authorityKeyIdentifier != it.subjectKeyIdentifier) {
        valid = true
      }
      authorityKeyIdentifier = it.authorityKeyIdentifier
    }
    return valid
  }

  String message = 'Invalid certificate chain; mismatched subject and authority key identifiers.'

}
