package com.capitaltg.thea.services;

import com.capitaltg.thea.certificates.CertificateChainValidator;
import com.capitaltg.thea.repositories.CertificateRepository;
import com.capitaltg.thea.util.CertificateUtil;
import com.capitaltg.thea.certificates.GullibleTrustManager;
import com.capitaltg.thea.certificates.TrustAnchorManager;
import com.capitaltg.thea.entities.Certificate;
import com.capitaltg.thea.entities.CertificateChain;
import com.capitaltg.thea.repositories.CertificateChainRepository;
// import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.CertificateRevokedException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXRevocationChecker;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

@Service
@Slf4j
public class CertificateService {

  @Autowired
  CertificateRepository certificateRepository;

  @Autowired
  CertificateChainRepository certificateChainRepository;

  @Autowired
  SessionFactory sessionFactory;

  @Autowired
  CertificateChainValidator validator;

  TrustAnchorManager trustAnchorManager = TrustAnchorManager.singleton();

  CertificateService() {
    System.setProperty("com.sun.net.ssl.checkRevocation", "true");
    Security.setProperty("ocsp.enable", "true");
  }

  public CertificateChain checkCertificateChain(String hostname, boolean hideResult) {
    try {
      CertPathBuilder certPathBuilder = CertPathBuilder.getInstance("PKIX");
      PKIXRevocationChecker revocationChecker = (PKIXRevocationChecker) certPathBuilder.getRevocationChecker();
      revocationChecker.setOptions(EnumSet.of(
          PKIXRevocationChecker.Option.PREFER_CRLS,
          PKIXRevocationChecker.Option.ONLY_END_ENTITY));

      Set<TrustAnchor> anchors = trustAnchorManager.getAllTrustedAnchors().stream()
          .map(it -> new TrustAnchor(it, null))
          .collect(Collectors.toSet());

      PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(anchors, new X509CertSelector());
      pkixParams.addCertPathChecker(revocationChecker);
      pkixParams.setRevocationEnabled(true);

      GullibleTrustManager gullibleTrustManager = new GullibleTrustManager();
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmf.init(new CertPathTrustManagerParameters(pkixParams));
      X509TrustManager defaultTrustManager = Arrays.stream(tmf.getTrustManagers())
          .filter(it -> it instanceof X509TrustManager)
          .map(it -> (X509TrustManager) it)
          .findFirst()
          .orElse(null);

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, new TrustManager[]{gullibleTrustManager, defaultTrustManager}, new SecureRandom());
      SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      URL url = new URL("https://" + hostname);
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.setConnectTimeout(5000);
      connection.setSSLSocketFactory(sslSocketFactory);

      List<String> warnings = new ArrayList<>();

      try {
        connection.connect();
        CertPathValidator validator = CertPathValidator.getInstance("PKIX");
        CertPath certPath = CertificateFactory.getInstance("X.509")
            .generateCertPath(gullibleTrustManager.getServerCertificates());
        validator.validate(certPath, pkixParams);
      } catch (CertificateException | SSLHandshakeException e) {
        if (e.getMessage().contains("CertificateException")) {
          warnings.add(StringUtils.substringAfterLast(e.getMessage(), "CertificateException: "));
        } else if (e.getMessage().contains("unrecognized_name")) {
          warnings.add("Server is not configured to respond to requests to " + hostname);
        } else {
          log.error("Failed to connect to " + hostname, e);
          warnings.add(e.getMessage());
        }
      } catch (SocketTimeoutException e) {
        warnings.add("Failed to connect due to timeout");
      } catch (CertPathValidatorException e) {
        if (e.getCause() instanceof CertificateRevokedException) {
          warnings.add(e.getCause().getMessage());
        } else if (e.getMessage().equals("Unable to determine revocation status due to network error")) {
          warnings.add(e.getMessage());
        } else {
          log.error("Failed to validate " + hostname, e);
        }
      } catch (ConnectException e) {
        log.warn("Unable to connect to " + hostname);
        warnings.add("Unable to connect to " + hostname);
      } catch (UnknownHostException e) {
        warnings.add(hostname + " is an unknown host");
      } catch (Exception e) {
        log.error("Failed to connect to " + hostname, e);
      }

      CertificateChain chain = new CertificateChain();
      chain.setHostname(hostname);
      chain.setCertificates(gullibleTrustManager.getServerCertificates().stream()
          .map(CertificateUtil::convertCertificate)
          .collect(Collectors.toList()));

      if (!chain.getCertificates().isEmpty()) {
        validator.validateCertificateChain(chain);
        chain.getWarnings().addAll(warnings);
        List<String> allWarnings = chain.getCertificates()
          .stream()
          .map(Certificate::getWarnings)
          .flatMap(List::stream)
          .collect(Collectors.toList());
        var isSuccess = chain.getWarnings().isEmpty() && allWarnings.isEmpty();
        chain.setSuccess(isSuccess);
        chain.setHideResult(hideResult);
        saveThem(chain);
      } else if (!warnings.isEmpty()) {
        chain.setWarnings(warnings);
      } else {
        chain.setWarnings(Collections.singletonList("Server does not support TLS connection"));
      }

      return chain;
    } catch (Exception e) {
      log.error("Failed to check certificate chain for " + hostname, e);
      return null;
    }
  }

  public CertificateChain getCertificateChainById(long id) {
    return certificateChainRepository.findById(id).orElse(null);
  }

  public Certificate getCertificate(String sha256) {
    return certificateRepository.findBySha256(sha256);
  }

  public List<Certificate> getCertificateChain(String sha256) throws NoSuchAlgorithmException, KeyStoreException {
    // TODO can probably use some cleanup
    Certificate certificate = certificateRepository.findBySha256(sha256);
    
    // TODO:  Preconditions.checkNotNull(certificate, "Certificate with SHA256 hash " + sha256 + " does not exist");

    List<List<Certificate>> listOfChains = new ArrayList<>();
    listOfChains.add(Collections.singletonList(certificate));
    boolean addedCerts = true;

    // build all possible, unexpired chains
    while (addedCerts) {
      addedCerts = false;
      List<List<Certificate>> newListOfChains = new ArrayList<>();
      for (List<Certificate> chain : listOfChains) {
        Certificate lastCertificate = chain.get(chain.size() - 1);
        if (lastCertificate.getAuthorityKeyIdentifier() != null &&
            !lastCertificate.getAuthorityKeyIdentifier().equals(lastCertificate.getSubjectKeyIdentifier())) {
          List<Certificate> authorities = certificateRepository.findBySubjectKeyIdentifier(lastCertificate.getAuthorityKeyIdentifier());
          for (Certificate authority : authorities) {
            if (authority.getNotAfter().isAfter(LocalDateTime.now())) {
              newListOfChains.add(new ArrayList<>(chain));
              newListOfChains.get(newListOfChains.size() - 1).add(authority);
              addedCerts = true;
            }
          }
        } else {
          newListOfChains.add(chain);
        }
      }
      listOfChains = newListOfChains;
    }

    List<String> trustedSkis = getTrustedAnchorCertificates().stream()
        .map(Certificate::getSubjectKeyIdentifier)
        .collect(Collectors.toList());

    // only consider chains that are anchored by trusted anchors
    // sort by length and return the longest one
    listOfChains = listOfChains.stream()
        .filter(chain -> trustedSkis.contains(chain.get(chain.size() - 1).getSubjectKeyIdentifier()))
        .sorted(Comparator.comparingInt(List::size))
        .collect(Collectors.toList());

    List<Certificate> chain = listOfChains.get(listOfChains.size() - 1);
    chain.remove(0);

    // remove the first certificate (original) and last (if a trust anchor)
    int last = chain.size();
    if (last == 0) {
      return chain;
    }
    Certificate lastCertificate = chain.get(last - 1);
    if (lastCertificate.getAuthorityKeyIdentifier() == null ||
        lastCertificate.getAuthorityKeyIdentifier().equals(lastCertificate.getSubjectKeyIdentifier())) {
      last--;
    }
    return chain.subList(0, last);
  }

  public List<Certificate> getSimilarCertificates(String sha256) {
    Certificate certificate = getCertificate(sha256);
    if (certificate == null || certificate.getSubjectKeyIdentifier() == null) {
      return new ArrayList<>();
    }
    List<Certificate> list = certificateRepository.findBySubjectKeyIdentifier(certificate.getSubjectKeyIdentifier());
    return list.stream().filter(c -> !c.getSha256().equals(sha256)).collect(Collectors.toList());
  }

  void saveThem(CertificateChain chain) {
    try (var session = sessionFactory.openSession()) {
      session.beginTransaction();
      for (Certificate certificate : chain.getCertificates()) {
        Certificate existing = session.bySimpleNaturalId(Certificate.class).load(certificate.getSha256());
        if (existing == null) {
          session.persist(certificate);
          existing = session.bySimpleNaturalId(Certificate.class).load(certificate.getSha256());
        }
        chain.getCertificates().set(chain.getCertificates().indexOf(certificate), existing);
      }
      session.persist(chain);
      session.getTransaction().commit();
    }
  }

  public List<Certificate> searchCertificates(Map<String, String> map) {
    String sql = "from Certificate order by commonName";
    var session = sessionFactory.openSession();

    List<String> clauses = map.entrySet().stream().map(entry -> {
      String k = entry.getKey();
      Object v = entry.getValue();
      if (k.toLowerCase().equals("subjectdn")) {
        return "( upper(" + k + ") like :" + k + " OR upper(subjectAlternativeNames) like cast(:" + k + " as string) )";
      } else {
        return "upper(" + k + ") like :" + k;
      }
    }).collect(Collectors.toList());

    String whereClause = String.join(" AND ", clauses);
    if (!clauses.isEmpty()) {
      sql = "from Certificate where " + whereClause + " order by commonName";
    }

    try {
      var query = session.createQuery(sql);
      map.forEach((k, v) -> query.setParameter(k, "%" + v.toString().toUpperCase() + "%"));
      return query.setMaxResults(20).list();
    } finally {
      session.close();
    }
  }

  public List<Certificate> getTrustedAnchorCertificates() throws NoSuchAlgorithmException, KeyStoreException {
    List<Certificate> list = trustAnchorManager.getAllTrustedAnchors()
      .stream()
      .map(CertificateUtil::convertCertificate)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    
    list.sort(Comparator.comparing(Certificate::getSubjectDn));
    
    return list;
  }


  public void saveCertificate(Certificate certificate) {
    var session = sessionFactory.openSession();
    try {
      Certificate existing = session.bySimpleNaturalId(Certificate.class).load(certificate.getSha256());
      if (existing == null) {
        session.save(certificate);
      }
    } finally {
      session.close();
    }
  }

  @PostConstruct
  public void storeTrustAnchorCertificates() throws CertificateParsingException, CertificateEncodingException, NoSuchAlgorithmException, IOException, KeyStoreException {
    certificateRepository.untrustAll();
    for (var trustAnchor : trustAnchorManager.getAllTrustedAnchors()) {
      Certificate certificate = new Certificate(trustAnchor);
      Certificate existing = getCertificate(certificate.getSha256());
      if (existing != null) {
        existing.setTrusted(true);
        log.info("Storing existing certificate {}", existing.getCommonName());
        certificateRepository.save(existing);
      } else {
        certificate.setTrusted(true);
        log.info("Storing certificate {}", certificate.getCommonName());
        certificateRepository.save(certificate);
      }
    }
  }

}
