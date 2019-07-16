package com.capitaltg.thea.validators.chain

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.validators.ChainValidator

class NoChainDuplicatesValidator implements ChainValidator {

  @Override
  boolean isInvalid(List<Certificate> certificates) {
    def list = certificates*.sha256.toUnique()
    return list.size() != certificates.size()
  }

  String message = 'Chain contains duplicate certificates'

}
