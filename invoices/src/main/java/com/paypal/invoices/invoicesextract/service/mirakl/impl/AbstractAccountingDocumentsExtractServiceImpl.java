package com.paypal.invoices.invoicesextract.service.mirakl.impl;

import com.mirakl.client.core.exception.MiraklApiException;
import com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentPaymentStatus;
import com.mirakl.client.mmp.domain.accounting.document.MiraklAccountingDocumentType;
import com.mirakl.client.mmp.domain.shop.MiraklShop;
import com.mirakl.client.mmp.domain.shop.MiraklShops;
import com.mirakl.client.mmp.operator.core.MiraklMarketplacePlatformOperatorApiClient;
import com.mirakl.client.mmp.operator.request.payment.invoice.MiraklGetInvoicesRequest;
import com.mirakl.client.mmp.request.payment.invoice.MiraklAccountingDocumentState;
import com.mirakl.client.mmp.request.shop.MiraklGetShopsRequest;
import com.paypal.infrastructure.converter.Converter;
import com.paypal.infrastructure.mail.MailNotificationUtil;
import com.paypal.infrastructure.util.MiraklLoggingErrorsUtil;
import com.paypal.invoices.invoicesextract.model.AccountingDocumentModel;
import com.paypal.invoices.invoicesextract.model.InvoiceTypeEnum;
import com.paypal.invoices.invoicesextract.service.mirakl.MiraklAccountingDocumentExtractService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public abstract class AbstractAccountingDocumentsExtractServiceImpl<T extends AccountingDocumentModel>
		implements MiraklAccountingDocumentExtractService<T> {

	protected final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter;

	protected final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient;

	protected final MailNotificationUtil invoicesMailNotificationUtil;

	protected AbstractAccountingDocumentsExtractServiceImpl(
			final Converter<MiraklShop, AccountingDocumentModel> miraklShopToAccountingModelConverter,
			final MiraklMarketplacePlatformOperatorApiClient miraklMarketplacePlatformOperatorApiClient,
			final MailNotificationUtil invoicesMailNotificationUtil) {
		this.miraklShopToAccountingModelConverter = miraklShopToAccountingModelConverter;
		this.miraklMarketplacePlatformOperatorApiClient = miraklMarketplacePlatformOperatorApiClient;
		this.invoicesMailNotificationUtil = invoicesMailNotificationUtil;
	}

	@Override
	public List<T> extractAccountingDocument(final Date delta) {
		final List<T> invoices = getAccountingDocument(delta);
		final Map<String, Pair<String, String>> mapShopDestinationToken = getMapDestinationTokens(invoices);

		return associateBillingDocumentsWithTokens(invoices, mapShopDestinationToken);
	}

	private Map<String, Pair<String, String>> getMapDestinationTokens(final List<T> creditNotes) {
		final MiraklShops shops = getMiraklShops(creditNotes);
		return mapShopsWithDestinationToken(shops);
	}

	@NonNull
	protected abstract List<T> associateBillingDocumentsWithTokens(final List<T> invoices,
			final Map<String, Pair<String, String>> mapShopDestinationToken);

	protected List<T> filterOnlyMappableDocuments(final List<T> invoices, final Set<String> shopIds) {

		//@formatter:off
        log.warn("Credit notes documents with ids [{}] should be skipped because are lacking hw-program or bank account token", invoices.stream()
                .filter(invoice -> !shopIds.contains(invoice.getShopId()))
                .map(AccountingDocumentModel::getInvoiceNumber)
                .collect(Collectors.joining(",")));
        //@formatter:on

		//@formatter:off
        return invoices.stream()
                .filter(invoice -> shopIds.contains(invoice.getShopId()))
                .collect(Collectors.toList());
        //@formatter:on
	}

	@NonNull
	protected MiraklShops getMiraklShops(final List<T> invoices) {
		final MiraklGetShopsRequest request = new MiraklGetShopsRequest();
		//@formatter:off
        final Set<String> shopsId = invoices.stream()
                .map(AccountingDocumentModel::getShopId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        request.setShopIds(shopsId);

        log.info("Retrieving information of shops [{}] for invoices [{}]", String.join(",", shopsId),
                invoices.stream()
                        .map(AccountingDocumentModel::getInvoiceNumber)
                        .collect(Collectors.joining(",")));
        //@formatter:on
		log.debug(ToStringBuilder.reflectionToString(request));
		try {
			return miraklMarketplacePlatformOperatorApiClient.getShops(request);
		}
		catch (final MiraklApiException ex) {
			log.error("Something went wrong getting information of shops [{}]", String.join(",", shopsId));
			invoicesMailNotificationUtil.sendPlainTextEmail("Issue detected getting shops in Mirakl",
					String.format("Something went wrong getting information of shops [%s]%n%s",
							String.join(",", shopsId), MiraklLoggingErrorsUtil.stringify(ex)));
			return new MiraklShops();
		}
	}

	protected Map<String, Pair<String, String>> mapShopsWithDestinationToken(final MiraklShops shops) {
		//@formatter:off
        return Stream.ofNullable(shops.getShops())
                .flatMap(Collection::stream)
                .map(miraklShopToAccountingModelConverter::convert)
                .filter(invoiceModel -> Objects.nonNull(invoiceModel.getDestinationToken()) && Objects.nonNull(invoiceModel.getHyperwalletProgram()))
                .collect(Collectors.toMap(AccountingDocumentModel::getShopId, accountingDocument -> Pair.of(accountingDocument.getDestinationToken(), accountingDocument.getHyperwalletProgram()), (i1, i2) -> i1));
        //@formatter:on
	}

	@NonNull
	protected MiraklGetInvoicesRequest createAccountingDocumentRequest(final Date delta,
			final InvoiceTypeEnum invoiceType) {
		final MiraklGetInvoicesRequest miraklGetInvoicesRequest = new MiraklGetInvoicesRequest();
		miraklGetInvoicesRequest.setStartDate(delta);
		miraklGetInvoicesRequest.setPaymentStatus(MiraklAccountingDocumentPaymentStatus.PENDING);
		miraklGetInvoicesRequest.addState(MiraklAccountingDocumentState.COMPLETE);
		miraklGetInvoicesRequest.setType(EnumUtils.getEnum(MiraklAccountingDocumentType.class, invoiceType.name()));

		return miraklGetInvoicesRequest;
	}

	protected abstract List<T> getAccountingDocument(final Date delta);

}
