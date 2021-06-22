package com.paypal.invoices.infraestructure.testing;

import com.paypal.invoices.invoicesextract.model.InvoiceModel;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
public class TestingInvoicesSessionDataHelper {

	private List<InvoiceModel> invoices;

	private boolean operatorCommissionsEnabled;

}
