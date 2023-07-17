package com.paypal;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EntityScan
@EnableWebSecurity
@EnableWebMvc
@EnableTransactionManagement
public class HyperwalletMiraklConnectorApplication {

	private static final String LINE_LOG_SEPARATOR = "\n----------------------------------------------------------\n\t";

	@Autowired(required = false)
	private BuildProperties buildProperties;

	public static void main(final String[] args) throws UnknownHostException {

		final SpringApplication app = new SpringApplication(HyperwalletMiraklConnectorApplication.class);
		final Environment env = app.run(args).getEnvironment();
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}

		//@formatter:off
		log.info(
				LINE_LOG_SEPARATOR
						+ "Application '{}:' is running! Access URLs:\n\t" + "Local: \t\t{}://{}:{}\n\t"
						+ "External: \t{}://{}:{}\n\t"
						+ "Mail host: \t{}\n\t"
						+ "Mail port: \t{}\n\t"
						+ "Mail SSL: \t{}\n\t"
						+ "Mail TLS: \t{}\n"
						+ "Encryption client public/private key location: \t{}\n"
						+ "Encryption client public key location: \t{}\n"
						+ "--------------------------------------------------------------",
				env.getProperty("spring.application.name"), protocol, InetAddress.getLoopbackAddress().getHostName(),
				env.getProperty("local.server.port"), protocol, InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("local.server.port"), env.getProperty("spring.mail.host"),
				env.getProperty("spring.mail.port"),
				env.getProperty("spring.mail.properties.mail.smtp.auth"),
				env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"),
				env.getProperty("hmc.hyperwallet.encryption.hmcKeySetLocation"),
				env.getProperty("hmc.hyperwallet.encryption.hmcPublicKeyLocation"));

		//@formatter:on
	}

	@PostConstruct
	public void printVersion() {
		if (buildProperties != null) {
			log.info(LINE_LOG_SEPARATOR + "Hyperwallet Mirakl Connector: Version [{}] \n\t" + LINE_LOG_SEPARATOR,
					buildProperties.getVersion());
		}
	}

}
