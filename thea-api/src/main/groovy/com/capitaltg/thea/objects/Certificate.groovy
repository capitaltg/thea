package com.capitaltg.thea.objects

import static com.capitaltg.thea.util.CertificateUtil.hexify

import com.capitaltg.thea.util.CertificateUtil
import com.capitaltg.thea.util.ListSerializer

import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.security.interfaces.ECPublicKey
import java.security.interfaces.RSAPublicKey
import java.time.LocalDateTime
import java.time.ZoneId

import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.StringUtils
import org.bouncycastle.asn1.x500.style.BCStyle
import org.bouncycastle.asn1.x500.style.IETFUtils
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.cert.X509CertificateHolder
import org.hibernate.annotations.NaturalId

@Entity
class Certificate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(unique = true, nullable = false)
  long certificateId

  @NaturalId
  @Column(length = 4000, unique = true, nullable = false)
  String sha256
  @Column(length = 500)
  String subjectDn
  String commonName
  String issuerDn
  String serialNumber
  String signatureAlgorithm
  String subjectKeyIdentifier
  String authorityKeyIdentifier
  boolean trusted
  boolean certificateAuthority
  LocalDateTime notBefore
  LocalDateTime notAfter
  @Column(length = 4000)
  String sha1
  @Column(length = 4000)
  String md5
  @Column(length = 16000)
  String base64Encoding
  String keyType
  Integer keyLength
  @Column(length = 4000)
  @Convert(converter = ListSerializer)
  List warnings = []
  @Column(length = 4000)
  @Convert(converter = ListSerializer)
  List subjectAlternativeNames

  Certificate() { }

  Certificate(X509Certificate certificate) {
    this.signatureAlgorithm = certificate.sigAlgName
    this.subjectDn = certificate.subjectDN
    this.issuerDn = certificate.issuerDN
    this.serialNumber = certificate.serialNumber.toString(16).toUpperCase()
    this.notBefore = LocalDateTime.ofInstant(certificate.notBefore.toInstant(), ZoneId.systemDefault())
    this.notAfter = LocalDateTime.ofInstant(certificate.notAfter.toInstant(), ZoneId.systemDefault())
    this.sha1 = hexify(MessageDigest.getInstance('SHA-1').digest( certificate.getEncoded() ))
    this.sha256 = hexify(MessageDigest.getInstance('SHA-256').digest( certificate.getEncoded() ))
    this.md5 = hexify(MessageDigest.getInstance('MD5').digest( certificate.getEncoded() ))
    this.base64Encoding = StringUtils.newStringUsAscii(Base64.encodeBase64(certificate.getEncoded(), true)).trim()

    if (certificate.publicKey instanceof RSAPublicKey) {
      RSAPublicKey rsaPk = (RSAPublicKey) certificate.publicKey
      this.keyType = 'RSAPublicKey'
      this.keyLength = rsaPk.modulus.bitLength()
    } else if (certificate.publicKey instanceof ECPublicKey) {
      ECPublicKey ecPublicKey = (ECPublicKey) certificate.publicKey
      this.keyType = 'ECPublicKey'
      this.keyLength = (ecPublicKey.encoded.length - 1) / 2 * 8
    }

    byte[] data = certificate.getEncoded()
    X509CertificateHolder holder = new X509CertificateHolder(data)
    def extensions = holder.extensions

    if (holder.subject.getRDNs(BCStyle.CN)) {
      commonName = IETFUtils.valueToString(holder.subject.getRDNs(BCStyle.CN)[0].first.value)
    }

    subjectKeyIdentifier = CertificateUtil.getSubjectKeyIdentifier(certificate)

    def value = extensions?.getExtensionParsedValue(Extension.authorityKeyIdentifier)
    if (value) {
      AuthorityKeyIdentifier aki = AuthorityKeyIdentifier.getInstance(value.toASN1Primitive().encoded)
      byte[] keyIdentifier = aki.getKeyIdentifier()
      authorityKeyIdentifier = hexify(keyIdentifier)
    }

    value = extensions?.getExtensionParsedValue(Extension.basicConstraints)
    if (value) {
      BasicConstraints bc = BasicConstraints.getInstance(value.toASN1Primitive().encoded)
      if (bc.isCA()) {
        this.certificateAuthority = true
      }
    }

    Collection<List<?>> alternativeNames = certificate.getSubjectAlternativeNames()
    this.subjectAlternativeNames = alternativeNames.findAll { it.get(0) in [0, 2] }.collect { it.toArray()[1] }
  }

}
