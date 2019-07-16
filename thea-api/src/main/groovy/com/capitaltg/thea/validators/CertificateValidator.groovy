package com.capitaltg.thea.validators

import com.capitaltg.thea.objects.Certificate

interface CertificateValidator {

  boolean isInvalid(Certificate certificate)
  String getMessage()

}
