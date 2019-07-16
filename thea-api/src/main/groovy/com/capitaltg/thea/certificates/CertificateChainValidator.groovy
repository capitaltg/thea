package com.capitaltg.thea.certificates

import com.capitaltg.thea.objects.CertificateChain
import com.capitaltg.thea.validators.CertificateValidator
import com.capitaltg.thea.validators.ChainValidator

import org.reflections.Reflections
import org.springframework.stereotype.Component

@Component
class CertificateChainValidator {

  List<CertificateValidator> certificateValidators
  List<ChainValidator> chainValidators

  CertificateChainValidator() {
    certificateValidators =
      new Reflections('com.capitaltg').getSubTypesOf(CertificateValidator)
        .collect { it.constructors[0] }*.newInstance()
    chainValidators =
      new Reflections('com.capitaltg').getSubTypesOf(ChainValidator)
        .collect { it.constructors[0] }*.newInstance()
  }

  def validateCertificateChain(CertificateChain chain) {
    // populate chain warnings
    chain.warnings = chainValidators.findAll { it.isInvalid(chain.certificates) }*.message

    // populate certificate warnings
    chain.certificates.each { def certificate ->
      certificate.warnings = certificateValidators.findAll { it.isInvalid(certificate) }*.message
    }
  }

}
