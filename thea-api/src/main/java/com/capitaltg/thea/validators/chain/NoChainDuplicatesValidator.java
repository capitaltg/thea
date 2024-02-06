package com.capitaltg.thea.validators.chain;

import java.util.List;
import java.util.stream.Collectors;

import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.ChainValidator;

public class NoChainDuplicatesValidator implements ChainValidator {

  @Override
  public boolean isInvalid(List<Certificate> certificates) {
    List<String> uniqueHashes = certificates.stream()
        .map(Certificate::getSha256)
        .distinct()
        .collect(Collectors.toList());
    return uniqueHashes.size() != certificates.size();
  }
  @Override
  public String getMessage() {
    return "Chain contains duplicate certificates";
  }

}
