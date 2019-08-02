package com.capitaltg.thea.config

import com.capitaltg.thea.objects.Certificate
import com.capitaltg.thea.objects.CertificateChain

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.orm.hibernate5.LocalSessionFactoryBean
import org.springframework.stereotype.Component

import javax.sql.DataSource

@Component
class DataConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataConfiguration)

  @Autowired
  DataSource dataSource

  @Bean(name='entityManagerFactory')
  LocalSessionFactoryBean getSessionFactory() {
    LOGGER.info('Initiating DataConfiguration')
    LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean()
    factoryBean.setDataSource(dataSource)
    Properties properties = new Properties()
    properties.setProperty('hibernate.dialect', 'org.hibernate.dialect.MySQL5Dialect')
    properties.setProperty('hibernate.show_sql', 'false')
    properties.setProperty('hibernate.hbm2ddl.auto', 'update')
    properties.setProperty('connection.autocommit', 'true')
    factoryBean.setAnnotatedClasses(Certificate, CertificateChain)
    factoryBean.setHibernateProperties(properties)
    LOGGER.info('Finished initiating DataConfiguration')
    return factoryBean
  }

}
