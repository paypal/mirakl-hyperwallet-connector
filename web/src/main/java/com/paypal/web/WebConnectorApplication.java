package com.paypal.web;

import com.paypal.infrastructure.InfrastructureConnectorApplication;
import com.paypal.invoices.InvoicesSpringContextConfiguration;
import com.paypal.kyc.KYCNotificationsSpringContextConfiguration;
import com.paypal.notifications.NotificationsSpringContextConfiguration;
import com.paypal.reports.ReportsSpringContextConfiguration;
import com.paypal.sellers.SellersSpringContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@ImportAutoConfiguration({ InfrastructureConnectorApplication.class, InvoicesSpringContextConfiguration.class,
		SellersSpringContextConfiguration.class, NotificationsSpringContextConfiguration.class,
		KYCNotificationsSpringContextConfiguration.class, ReportsSpringContextConfiguration.class })
public class WebConnectorApplication {

	private static ConfigurableApplicationContext context;

	public static void main(final String[] args) throws UnknownHostException {

		final SpringApplication app = new SpringApplication(WebConnectorApplication.class);
		final Environment env = app.run(args).getEnvironment();
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}

		//@formatter:off
		log.info(
				"\n----------------------------------------------------------\n\t"
						+ "Application '{}:' is running! Access URLs:\n\t" + "Local: \t\t{}://{}:{}\n\t"
						+ "External: \t{}://{}:{}\n\t"
						+ "Profile(s): \t{}\n\t"
						+ "Mail host: \t{}\n\t"
						+ "Mail port: \t{}\n\t"
						+ "Mail SSL: \t{}\n\t"
						+ "Mail TLS: \t{}\n"
						+ "Encryption client public/private key location: \t{}\n"
						+ "Encryption client public key location: \t{}\n"
						+ "----------------------------------------------------------",
				env.getProperty("spring.application.name"), protocol, InetAddress.getLoopbackAddress().getHostName(),
				env.getProperty("local.server.port"), protocol, InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("local.server.port"), env.getActiveProfiles(), env.getProperty("spring.mail.host"),
				env.getProperty("spring.mail.port"),
				env.getProperty("spring.mail.properties.mail.smtp.auth"),
				env.getProperty("spring.mail.properties.mail.smtp.starttls.enable"),
				env.getProperty("hyperwallet.api.hmcKeySetLocation"),
				env.getProperty("hyperwallet.api.hmcPublicKeyLocation"));

		//@formatter:on
	}

}
