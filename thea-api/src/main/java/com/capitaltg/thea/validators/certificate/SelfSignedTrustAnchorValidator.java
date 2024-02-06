package com.capitaltg.thea.validators.certificate;

import com.capitaltg.thea.certificates.TrustAnchorManager;
import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.validators.CertificateValidator;

public class SelfSignedTrustAnchorValidator implements CertificateValidator {

  @Override
  public boolean isInvalid(Certificate certificate) {
    return (certificate.getSubjectDn().equals(certificate.getIssuerDn()) || certificate.getAuthorityKeyIdentifier() == null) &&
      TrustAnchorManager.singleton().isTrustedAnchor(certificate.getSubjectKeyIdentifier());
  }

  @Override
  public String getMessage() {
    return "Certificate is a self-signed trust anchor";
  }

}
