package com.capitaltg.thea.validators;

import com.capitaltg.thea.entities.Certificate;

public interface CertificateValidator {

  boolean isInvalid(Certificate certificate);
  String getMessage();

}
