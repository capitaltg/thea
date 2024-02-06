package com.capitaltg.thea.certificates;

import com.capitaltg.thea.entities.CertificateChain;
import com.capitaltg.thea.validators.CertificateValidator;
import com.capitaltg.thea.validators.ChainValidator;
import com.capitaltg.thea.validators.certificate.CertificateExpiredValidator;
import com.capitaltg.thea.validators.certificate.MissingSANValidator;
import com.capitaltg.thea.validators.certificate.SelfSignedTrustAnchorValidator;
import com.capitaltg.thea.validators.certificate.Sha1SignatureValidator;
import com.capitaltg.thea.validators.chain.ContainsExpiredCertificateValidator;
import com.capitaltg.thea.validators.chain.ContainsSelfSignedValidator;
import com.capitaltg.thea.validators.chain.ExpiringCertificateInChainValidator;
import com.capitaltg.thea.validators.chain.MatchingAuthorityValidator;
import com.capitaltg.thea.validators.chain.NoChainDuplicatesValidator;
import com.capitaltg.thea.validators.chain.TrustedChainValidator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class CertificateChainValidator {

  List<CertificateValidator> certificateValidators;
  List<ChainValidator> chainValidators;

  public CertificateChainValidator() {
    certificateValidators = Arrays.asList(new CertificateExpiredValidator(), new MissingSANValidator(),
        new SelfSignedTrustAnchorValidator(), new Sha1SignatureValidator());
    chainValidators = Arrays.asList(new ContainsExpiredCertificateValidator(), new ContainsSelfSignedValidator(),
        new ExpiringCertificateInChainValidator(), new MatchingAuthorityValidator(), new NoChainDuplicatesValidator(),
        new TrustedChainValidator());
  }

  public void validateCertificateChain(CertificateChain chain) {

    List<String> warnings = chainValidators.stream()
        .filter(validator -> validator.isInvalid(chain.getCertificates()))
        .map(validator -> validator.getMessage())
        .collect(Collectors.toList());
    chain.setWarnings(warnings);

    chain.getCertificates().forEach(certificate -> {
      List<String> certWarnings = certificateValidators.stream()
          .filter(validator -> validator.isInvalid(certificate))
          .map(validator -> validator.getMessage())
          .collect(Collectors.toList());
      certificate.setWarnings(certWarnings);
    });
  }

}
