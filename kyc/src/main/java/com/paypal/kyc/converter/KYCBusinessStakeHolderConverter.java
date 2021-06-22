package com.paypal.kyc.converter;

import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.paypal.kyc.model.KYCDocumentBusinessStakeHolderInfoModel;
import org.springframework.lang.NonNull;

/**
 * Interface to convert business stakeholder objects from source class {@link MiraklShop}
 * into target class {@link KYCDocumentBusinessStakeHolderInfoModel}
 */
public interface KYCBusinessStakeHolderConverter {

	/**
	 * Method that retrieves a {@link MiraklShop} and returns a
	 * {@link KYCDocumentBusinessStakeHolderInfoModel}
	 * @param source the source object {@link MiraklShop}
	 * @param businessStakeholderNumber business stakeholder position in the source
	 * @return the returned object {@link KYCDocumentBusinessStakeHolderInfoModel}
	 */
	KYCDocumentBusinessStakeHolderInfoModel convert(@NonNull final MiraklShop source, int businessStakeholderNumber);

}
