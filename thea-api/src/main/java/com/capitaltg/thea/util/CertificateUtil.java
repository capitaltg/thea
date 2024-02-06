package com.capitaltg.thea.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;

import com.capitaltg.thea.entities.Certificate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CertificateUtil {

  public static String getSubjectKeyIdentifier(X509Certificate certificate) {
    try {
      byte[] data = certificate.getEncoded();
      X509CertificateHolder holder = new X509CertificateHolder(data);
      var extensions = holder.getExtensions();
      if (extensions != null) {
        var value = extensions.getExtensionParsedValue(Extension.subjectKeyIdentifier);
        if (value != null) {
          SubjectKeyIdentifier ski = SubjectKeyIdentifier.getInstance(value.toASN1Primitive().getEncoded());
          byte[] keyIdentifier = ski.getKeyIdentifier();
          return hexify(keyIdentifier);
        }
      }
    } catch (CertificateEncodingException | IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String hexify(byte[] array) {
    BigInteger big = new BigInteger(1, array);
    String string = big.toString(16).toUpperCase(Locale.ENGLISH);
    return StringUtils.leftPad(string, string.length() % 2 + string.length(), "0");
  }

  public static X509Certificate loadCertificate(String resourceName) throws CertificateException {
    var factory = CertificateFactory.getInstance("X.509");
    var stream = CertificateUtil.class.getClassLoader().getResourceAsStream(resourceName);
    return (X509Certificate) factory.generateCertificate(stream);
  }

  public static Certificate convertCertificate(X509Certificate cert) {
    try {
      return new Certificate(cert);
    } catch (Exception e) {
      log.error("Failed to convert certificate " + cert, e);
      return null;
    }
  }

}
