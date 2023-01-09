package com.paypal.infrastructure.configuration;

import com.paypal.infrastructure.exceptions.InvalidConfigurationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public abstract class AbstractProgramHierarchyHyperwalletApiConfig extends AbstractHyperwalletEncryptionApiConfig {

	@Resource
	protected Environment environment;

	@Value("#{'${hyperwallet.api.hyperwalletprograms}'.split(',')}")
	protected List<String> hyperwalletPrograms;

	@Value("#{'${hyperwallet.api.hyperwalletprograms.ignored}'.split(',')}")
	private List<String> ignoredHyperwalletPrograms;

	protected Map<String, String> loadTokenMapConfiguration(@NonNull final String hyperwalletProgramPrefix)
			throws InvalidConfigurationException {
		Map<String, String> tokenMap = new HashMap<>();
		if (Objects.nonNull(hyperwalletPrograms)) {
			//@formatter:off
			try {
				tokenMap = Optional.of(hyperwalletPrograms).orElse(List.of())
						.stream()
						.map(tokenKey -> Pair.of(tokenKey, hyperwalletProgramPrefix.concat(tokenKey)))
						.map(tokenKeyAndTokenKeyPrefix -> Pair.of(tokenKeyAndTokenKeyPrefix.getLeft(),
								environment.getProperty(tokenKeyAndTokenKeyPrefix.getRight())))
						.collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
			}
			catch (final NullPointerException ex) {
				throw new InvalidConfigurationException("Property files configuration error. Check hyperwallet program tokens configuration. Are all parameters defined for: '" + hyperwalletProgramPrefix + "*'?");
			}
			//@formatter:on

		}
		return tokenMap;
	}

	@PostConstruct
	void removeEmptyProgram() {
		ignoredHyperwalletPrograms = removeEmptyStringValues(ignoredHyperwalletPrograms);
		hyperwalletPrograms = removeEmptyStringValues(hyperwalletPrograms);
	}

	private List<String> removeEmptyStringValues(final List<String> ignoredHyperwalletPrograms) {
		return ignoredHyperwalletPrograms.stream().filter(program -> !program.equals("")).collect(Collectors.toList());
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof AbstractProgramHierarchyHyperwalletApiConfig)) {
			return false;
		}
		final AbstractProgramHierarchyHyperwalletApiConfig that = (AbstractProgramHierarchyHyperwalletApiConfig) o;

		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
