package com.capitaltg.thea.validators.chain

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.ChainValidator

import com.capitaltg.thea.certificates.TrustAnchorManager

class TrustedChainValidator implements ChainValidator {

  List subjectKeyIdentifiers = TrustAnchorManager.singleton().subjectKeyIdentifiers

  @Override
  boolean isInvalid(List<Certificate> certificates) {
    !certificates.find { it.authorityKeyIdentifier in subjectKeyIdentifiers }
  }

  String message = 'Chain is not anchored by trusted certificate authority'

}
