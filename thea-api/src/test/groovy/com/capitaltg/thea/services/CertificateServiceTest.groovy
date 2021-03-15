package com.capitaltg.thea.services

import java.security.KeyStore

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.util.CertificateUtil

@RunWith(SpringRunner)
@SpringBootTest
class CertificateServiceTest {

  @Autowired
  CertificateService certificateService

  @Test
  void checkCertificateChain() {
    def server = startServer('/orion.jks')
    def hostname = 'localhost:8443'
    def certificateChain = certificateService.checkCertificateChain(hostname)
    assert !certificateChain.success
    assert certificateChain.hostname == hostname
    assert certificateChain.warnings.contains('No subject alternative DNS name matching localhost found.')
    server.stop()
  }

  @Ignore('the values here change based on the JVM. need to update to be more generic.')
  @Test
  void searchCertificates() {
    def certificates = certificateService.searchCertificates([subjectDn:'baltimore'])
    assert certificates.size() == 2
    assert certificates[0].serialNumber == '20000BF'
  }

  @Test
  void searchCertificatesAll() {
    def certificates = certificateService.searchCertificates([:])
    assert certificates.size() > 1
  }

  @Test
  void getTrustedAnchorCertificates() {
    def certificates = certificateService.getTrustedAnchorCertificates()
    assert certificates
  }

  @Test
  void getSimilarCertificates() {
    def certificates = certificateService
      .getSimilarCertificates('16AF57A9F676B0AB126095AA5EBADEF22AB31119D644AC95CD4B93DBF3F26AEB')
    assert !certificates
  }

  @Test
  void revoked() {
    def hostname = 'revoked.badssl.com'
    def certificateChain = certificateService.checkCertificateChain(hostname)
    assert certificateChain.hostname == hostname
    assert certificateChain.warnings
    assert !certificateChain.success
  }

  @Test
  void unknownHost() {
    def hostname = 'some-unknown-host.unknown'
    def certificateChain = certificateService.checkCertificateChain(hostname)
    assert certificateChain.hostname == hostname
    assert certificateChain.warnings
    assert !certificateChain.success
  }

  @Test
  void getCertificateChain() {
    def server = startServer('/localhost.jks')
    def hostname = 'localhost:8443'
    certificateService.checkCertificateChain(hostname)
    def certificates = certificateService
      .getCertificateChain('C9C93A25D2142ED75E138D1CA12EBD00BBBCEF8905CADB572823015294BAB497')
    assert certificates.size() == 1
    server.stop()
  }

  @Test
  void getBestCertificateChain() {
    def list = ['COMODO_RSA_CA_expired.cer', 'datausa.io.cer', 'COMODO_RSA.cer', 'COMODO_RSA_CA.cer',
      'COMODO_RSA_CA_cross_signed.cer', 'comodo-cross.cer']
    list.each { name ->
      def certificate = new Certificate(CertificateUtil.loadCertificate(name))
      certificateService.saveCertificate(certificate)
    }
    def certificates = certificateService
      .getCertificateChain('520A98441167C5E66B29AEC787C71034D5311FE89819849AC30C68CD6F27DB4B')

    assert certificates.size() == 2
    def last = certificates.last()
    assert last.serialNumber == '67DEF43EF17BDAE24FF5940606D2C084'
  }

  @Test
  void getCertificateChainEmpty() {
    def certificates = certificateService
      .getCertificateChain('16AF57A9F676B0AB126095AA5EBADEF22AB31119D644AC95CD4B93DBF3F26AEB')
    assert !certificates
  }

  @Test
  void missingSan() {
    def server = startServer('/localhost.jks')
    def chain = certificateService.checkCertificateChain('localhost:8443')
    assert chain.certificates[0].warnings == ['Certificate does not contain any subject alternative names']
    server.stop()
  }

  private Server startServer(def resource) {
    def keyStore = KeyStore.getInstance('JKS')
    def inputStream = CertificateServiceTest.getResourceAsStream(resource)
    keyStore.load(inputStream, 'password'.toCharArray())

    def sslContextFactory = new SslContextFactory.Server()
    sslContextFactory.setKeyStore(keyStore)
    sslContextFactory.setKeyStorePassword('password')

    Server server = new Server()
    ServerConnector sslConnector = new ServerConnector(server, sslContextFactory)
    sslConnector.port = 8443
    server.addConnector(sslConnector)
    server.start()
    return server
  }

}
