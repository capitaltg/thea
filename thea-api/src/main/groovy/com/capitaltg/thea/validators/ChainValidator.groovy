package com.capitaltg.thea.validators

import com.capitaltg.thea.objects.Certificate

interface ChainValidator {

  boolean isInvalid(List<Certificate> certificates)
  String getMessage()

}
