package com.capitaltg.thea.validators;

import java.util.List;

import com.capitaltg.thea.entities.Certificate;

public interface ChainValidator {

  public boolean isInvalid(List<Certificate> certificates);
  public String getMessage();

}
