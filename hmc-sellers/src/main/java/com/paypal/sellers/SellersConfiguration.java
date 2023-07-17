package com.paypal.sellers;

import com.paypal.sellers.sellerextractioncommons.configuration.SellersMiraklApiConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableConfigurationProperties({ SellersMiraklApiConfig.class })
public class SellersConfiguration {

}
