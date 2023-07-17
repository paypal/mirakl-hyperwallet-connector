package com.paypal.testsupport;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class TestDateUtilTest {

	private TestDateUtil testObj;

	@Test
	void from() {
		assertThat(TestDateUtil.from("2020-01-01")).isEqualTo("2020-01-01");
	}

	@Test
	void testFrom() {
		assertThat(TestDateUtil.from("yyyy-dd-MM", "2020-31-01")).isEqualTo("2020-01-31");
	}

	@Test
	void withinInterval() {
		assertThat(TestDateUtil.withinInterval(new Date(), 1)).isTrue();
	}

	@Test
	void currentDateMinusDays() {
		final Date currentDateMinus1Day = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
		assertThat(TestDateUtil.currentDateMinusDays(1)).isEqualTo(currentDateMinus1Day);
	}

	@Test
	void currentDateMinusDaysPlusSeconds() {
		final Date currentDateMinus1Day = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000 + 1000);
		assertThat(TestDateUtil.currentDateMinusDaysPlusSeconds(1, 1)).isEqualTo(currentDateMinus1Day);
	}

}
