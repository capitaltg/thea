package com.capitaltg.thea.entities;

import static com.capitaltg.thea.util.CertificateUtil.hexify;

import com.capitaltg.thea.util.CertificateUtil;
import com.capitaltg.thea.util.ListSerializer;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.binary.Base64;

import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.hibernate.annotations.NaturalId;

@Entity
@Getter
@Setter
@Table(name = "Certificate")
public class Certificate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  long certificateId;

  @NaturalId
  @Column(columnDefinition = "TEXT", unique = true, nullable = false)
  String sha256;
  @Column(columnDefinition = "TEXT")
  String subjectDn;
  String commonName;
  String issuerDn;
  String serialNumber;
  String signatureAlgorithm;
  String subjectKeyIdentifier;
  String authorityKeyIdentifier;
  boolean trusted;
  boolean certificateAuthority;
  LocalDateTime notBefore;
  LocalDateTime notAfter;
  @Column(columnDefinition = "TEXT")
  String sha1;
  @Column(columnDefinition = "TEXT")
  String md5;
  @Column(columnDefinition = "TEXT")
  String base64Encoding;
  String keyType;
  Integer keyLength;
  @Column(columnDefinition = "TEXT")
  @Convert(converter = ListSerializer.class)
  List<String> warnings = new ArrayList<>();
  @Column(columnDefinition = "TEXT")
  @Convert(converter = ListSerializer.class)
  List<String> subjectAlternativeNames;

  public Certificate() {
  }

  public Certificate(X509Certificate certificate)
      throws IOException, CertificateParsingException, CertificateEncodingException, NoSuchAlgorithmException {
    this.signatureAlgorithm = certificate.getSigAlgName();
    this.subjectDn = certificate.getSubjectDN().getName().replaceAll("[^\\p{ASCII}]", "?");
    this.issuerDn = certificate.getIssuerDN().getName().replaceAll("[^\\p{ASCII}]", "?");
    this.serialNumber = certificate.getSerialNumber().toString(16).toUpperCase();
    this.notBefore = LocalDateTime.ofInstant(certificate.getNotBefore().toInstant(), ZoneId.systemDefault());
    this.notAfter = LocalDateTime.ofInstant(certificate.getNotAfter().toInstant(), ZoneId.systemDefault());
    this.sha1 = hexify(MessageDigest.getInstance("SHA-1").digest(certificate.getEncoded()));
    this.sha256 = hexify(MessageDigest.getInstance("SHA-256").digest(certificate.getEncoded()));
    this.md5 = hexify(MessageDigest.getInstance("MD5").digest(certificate.getEncoded()));
    this.base64Encoding = StringUtils.newStringUsAscii(Base64.encodeBase64(certificate.getEncoded(), true)).trim();

    if (certificate.getPublicKey() instanceof RSAPublicKey) {
      RSAPublicKey rsaPk = (RSAPublicKey) certificate.getPublicKey();
      this.keyType = "RSAPublicKey";
      this.keyLength = rsaPk.getModulus().bitLength();
    } else if (certificate.getPublicKey() instanceof ECPublicKey) {
      ECPublicKey ecPublicKey = (ECPublicKey) certificate.getPublicKey();
      this.keyType = "ECPublicKey";
      this.keyLength = (ecPublicKey.getEncoded().length - 1) / 2 * 8;
    }

    byte[] data = certificate.getEncoded();
    X509CertificateHolder holder = new X509CertificateHolder(data);
    var extensions = holder.getExtensions();

    commonName = extractCommonName(certificate);

    subjectKeyIdentifier = CertificateUtil.getSubjectKeyIdentifier(certificate);

    if (extensions != null) {
      var value = extensions.getExtensionParsedValue(Extension.authorityKeyIdentifier);
      if (value != null) {
        AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(value.toASN1Primitive().getEncoded());
        byte[] keyIdentifier = aki.getKeyIdentifier();
        authorityKeyIdentifier = hexify(keyIdentifier);
      }

      value = extensions.getExtensionParsedValue(Extension.basicConstraints);
      if (value != null) {
        BasicConstraints bc = BasicConstraints.getInstance(value.toASN1Primitive().getEncoded());
        if (bc.isCA()) {
          this.certificateAuthority = true;
        }
      }
    }

    Collection<List<?>> alternativeNames = certificate.getSubjectAlternativeNames();
    if (alternativeNames != null) {
      this.subjectAlternativeNames = alternativeNames.stream()
          .map(list -> list.get(1).toString())
          .collect(Collectors.toList());
    } else {
      subjectAlternativeNames = new ArrayList<>();
    }
  }

  public String extractCommonName(X509Certificate certificate) {
    String commonName = null;
    if (certificate.getSubjectDN().getName().contains("CN=")) {
      commonName = certificate.getSubjectDN().getName().split("CN=")[1].split(",")[0];
      return commonName.replaceAll("[^\\p{ASCII}]", "?");
    }
    return commonName;
  }

}
