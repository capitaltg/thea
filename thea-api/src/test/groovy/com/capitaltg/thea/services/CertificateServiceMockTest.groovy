package com.capitaltg.thea.services

import static org.junit.Assert.fail

import static org.mockito.Mockito.times
import static org.mockito.Mockito.verify

import com.capitaltg.thea.certificates.CertificateRepository
import com.capitaltg.thea.repositories.CertificateChainRepository

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner)
class CertificateServiceMockTest {

  @InjectMocks
  CertificateService certificateService

  @Mock
  CertificateRepository certificateRepository

  @Mock
  CertificateChainRepository certificateChainRepository

  @Test
  void getCertificate() {
    def sha256 = 'aaaaaaaaaaaa'
    certificateService.getCertificate(sha256)
    verify(certificateRepository).findBySha256(sha256)
  }

  @Test
  void getCertificateChainById() {
    def id = 1L
    certificateService.getCertificateChainById(id)
    verify(certificateChainRepository).findById(id)
  }

  @Test
  void getCertificateChain() {
    def sha256 = '3E9099B5015E8F486C00BCEA9D111EE721FABA355A89BCF1DF69561E3DC6325C'
    try {
      certificateService.getCertificateChain(sha256)
      fail('Should throw a NPE since certificate does not exist')
    } catch (Exception e) {
      verify(certificateRepository).findBySha256(sha256)
    }
  }

  @Test
  void getSimilarCertificates() {
    def sha256 = 'aaaaaaaaaaaa'
    certificateService.getSimilarCertificates(sha256)
    // trust anchor certificates aren't yet loaded, so findBySubjectKeyIdentifier
    // wont't be called
    verify(certificateRepository, times(0)).findBySubjectKeyIdentifier(sha256)
  }

}
