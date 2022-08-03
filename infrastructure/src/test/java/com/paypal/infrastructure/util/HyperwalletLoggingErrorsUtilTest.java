package com.paypal.infrastructure.util;

import cc.protea.util.http.Response;
import com.hyperwallet.clientsdk.HyperwalletException;
import com.hyperwallet.clientsdk.model.HyperwalletError;
import com.hyperwallet.clientsdk.model.HyperwalletErrorList;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class HyperwalletLoggingErrorsUtilTest {

	@Test
	void stringify_shouldPrintAllExceptionFields_forHyperwalletExceptionTypeA() {
		HyperwalletException hyperwalletException = buildHyperwalletExceptionTypeA();
		String result = HyperwalletLoggingErrorsUtil.stringify(hyperwalletException);

		//@formatter:off
        assertThat(result).isEqualTo("""
                {
                  "errorCode" : "400",
                  "errorMessage" : "HYPERWALLET_MESSAGE",
                  "exceptionMessage" : "HYPERWALLET_MESSAGE",
                  "responseStatusCode" : "200",
                  "responseMessage" : "RESPONSE_MESSAGE",
                  "responseBody" : "{field1:\\"value1\\"}"
                }""");
        //@formatter:om
    }

    @Test
    void stringify_shouldPrintAllExceptionFields_forHyperwalletExceptionTypeB() {
        HyperwalletException hyperwalletException = buildHyperwalletExceptionTypeB();
        String result = HyperwalletLoggingErrorsUtil.stringify(hyperwalletException);

        //@formatter:off
        assertThat(result).isEqualTo("""
                {
                  "errorCode" : "CODE-0",
                  "errorMessage" : "MESSAGE-0",
                  "exceptionMessage" : "MESSAGE-0",
                  "responseStatusCode" : "200",
                  "responseMessage" : "RESPONSE_MESSAGE",
                  "responseBody" : "RESPONSE_BODY",
                  "errorDetailList" : [ {
                    "code" : "CODE-0",
                    "fieldName" : "FIELD-0",
                    "message" : "MESSAGE-0",
                    "relatedResources" : [ "RELATED_RESOURCE-0-1", "RELATED_RESOURCE-101" ]
                  }, {
                    "code" : "CODE-1",
                    "fieldName" : "FIELD-1",
                    "message" : "MESSAGE-1",
                    "relatedResources" : [ "RELATED_RESOURCE-1-1", "RELATED_RESOURCE-111" ]
                  } ]
                }""");
        //@formatter:om
    }

    @Test
    void stringify_shouldPrintAllExceptionFields_forHyperwalletExceptionTypeC() {
        HyperwalletException hyperwalletException = buildHyperwalletExceptionTypeC();
        String result = HyperwalletLoggingErrorsUtil.stringify(hyperwalletException);

        //@formatter:off
        assertThat(result).isEqualTo("""
                {
                  "exceptionMessage" : "java.lang.RuntimeException: EXCEPTION_MESSAGE"
                }""");
        //@formatter:on
	}

	private HyperwalletException buildHyperwalletExceptionTypeA() {
		Response response = new Response();
		response.setResponseCode(200);
		response.setResponseMessage("RESPONSE_MESSAGE");
		response.setBody("{field1:\"value1\"}");
		return new HyperwalletException(response, 400, "HYPERWALLET_MESSAGE");
	}

	private HyperwalletException buildHyperwalletExceptionTypeB() {
		Response response = new Response();
		response.setResponseCode(200);
		response.setResponseMessage("RESPONSE_MESSAGE");
		response.setBody("RESPONSE_BODY");
		return new HyperwalletException(response, buildHyperwalletErrorList(2));
	}

	private HyperwalletException buildHyperwalletExceptionTypeC() {
		RuntimeException e = new RuntimeException("EXCEPTION_MESSAGE");
		return new HyperwalletException(e);
	}

	private HyperwalletErrorList buildHyperwalletErrorList(int num) {
		HyperwalletErrorList hyperwalletErrorList = new HyperwalletErrorList();
		//@formatter:off
        hyperwalletErrorList.setErrors(
                IntStream.range(0, num)
                        .mapToObj(this::buildHyperwalletError)
                        .collect(Collectors.toList()));
        //@formatter:on
		return hyperwalletErrorList;
	}

	private HyperwalletError buildHyperwalletError(int i) {
		//@formatter:off
        return new HyperwalletError("CODE-" + i,
            "FIELD-" + i,
            "MESSAGE-" + i,
            List.of("RELATED_RESOURCE-" + i + - 1, "RELATED_RESOURCE-1" + i + 1));
        //@formatter:on
	}

}