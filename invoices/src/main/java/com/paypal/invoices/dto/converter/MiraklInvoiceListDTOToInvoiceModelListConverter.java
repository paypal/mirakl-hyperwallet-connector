package com.paypal.invoices.dto.converter;

import com.paypal.infrastructure.converter.Converter;
import com.paypal.invoices.dto.MiraklInvoicesMockListDTO;
import com.paypal.invoices.invoicesextract.model.InvoiceModel;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class MiraklInvoiceListDTOToInvoiceModelListConverter
		implements Converter<MiraklInvoicesMockListDTO, List<InvoiceModel>> {

	@Resource
	private MiraklInvoiceDTOToInvoiceModelConverter miraklInvoiceDTOToInvoiceModelConverter;

	@Override
	public List<InvoiceModel> convert(final MiraklInvoicesMockListDTO source) {

		//@formatter:off
		final MiraklInvoicesMockListDTO defaultResult = new MiraklInvoicesMockListDTO();
		defaultResult.setInvoices(Collections.emptyList());

		return Optional.ofNullable(source)
				.orElse(defaultResult)
				.getInvoices().stream()
				.map(miraklInvoiceDTOToInvoiceModelConverter::convert)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		//@formatter:on
	}

}
