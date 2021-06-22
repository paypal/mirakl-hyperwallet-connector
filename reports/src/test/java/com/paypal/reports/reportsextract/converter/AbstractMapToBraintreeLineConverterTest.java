package com.paypal.reports.reportsextract.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AbstractMapToBraintreeLineConverterTest {

	private MyAbstractMapToBraintreeLineConverter testObj;

	private File edgeJsonWithOrderId;

	@BeforeEach
	void setUp() {
		testObj = new MyAbstractMapToBraintreeLineConverter();
	}

	@Test
	void convert_shouldReturnPopulatedHMCBraintreeTransactionLineWhenInputDataIsAValidEdgeMapWithOrderIdFilled()
			throws IOException {
		edgeJsonWithOrderId = Paths.get("src", "test", "resources", "graphQLEdgeWithOrderId.json").toFile();
		final Map<String, Object> edge = new ObjectMapper().readValue(edgeJsonWithOrderId, Map.class);

		final var result = testObj.getBraintreeNodeGraphQLModel(edge);

		BigDecimal expected = BigDecimal.valueOf(108.84);
		expected = expected.setScale(2);
		assertThat(result.getAmount().getValue()).isEqualTo(expected);
		assertThat(result.getCreatedAt()).isEqualTo("2021-05-13T06:56:03.000000Z");
		assertThat(result.getAmount().getCurrencyCode()).isEqualTo("USD");
		assertThat(result.getOrderId()).isEqualTo("854201000");
	}

	@Test
	void convert_shouldReturnNullWhenInputMapIsNull() {
		final var result = testObj.getBraintreeNodeGraphQLModel(null);

		assertThat(result).isNull();
	}

	private class MyAbstractMapToBraintreeLineConverter extends AbstractMapToBraintreeLineConverter {

	}

}
