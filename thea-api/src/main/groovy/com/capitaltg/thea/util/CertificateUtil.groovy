package com.capitaltg.thea.util

import java.security.cert.X509Certificate

import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier
import org.bouncycastle.cert.X509CertificateHolder

class CertificateUtil {

  static String getSubjectKeyIdentifier(X509Certificate certificate) {
    byte[] data = certificate.getEncoded()
    X509CertificateHolder holder = new X509CertificateHolder(data)
    def extensions = holder.extensions
    def value = extensions?.getExtensionParsedValue(Extension.subjectKeyIdentifier)
    if (value) {
      SubjectKeyIdentifier ski = SubjectKeyIdentifier.getInstance(value.toASN1Primitive().encoded)
      byte[] keyIdentifier = ski.getKeyIdentifier()
      return hexify(keyIdentifier)
    }
  }

  static String hexify(byte[] array) {
    BigInteger big = new BigInteger(1, array)
    String string = big.toString(16).toUpperCase(Locale.ENGLISH)
    return org.apache.commons.lang3.StringUtils.leftPad(string, string.size() % 2 + string.size(), '0')
  }

}
