package com.paypal.kyc.incomingnotifications.model;

import com.mirakl.client.mmp.domain.shop.document.MiraklShopDocument;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Model that manages notifications used to remove documents when KYC-verification is
 * needed
 */
@Getter
@SuperBuilder
public class KYCUserDocumentsNotificationBodyModel extends KYCNotificationBodyModel {

	protected final transient List<MiraklShopDocument> miraklShopDocuments;

}
