package com.paypal.observability.miraklfieldschecks.repository;

import com.paypal.infrastructure.support.exceptions.HMCException;
import com.paypal.observability.miraklfieldschecks.repository.model.MiraklFieldSchemaYaml;
import com.paypal.observability.miraklfieldschecks.repository.model.MiraklSchemaGroupYaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class MiraklFieldSchemaRepositoryImpl implements MiraklFieldSchemaRepository {

	@Value("classpath:mirakl/customfield-schemas/*")
	private Resource[] resources;

	@Override
	public MiraklFieldSchemaYaml loadCustomFieldsSchema(final boolean includeKycRequiredFields) {
		return new MiraklFieldSchemaYaml(loadCustomFieldGroups(includeKycRequiredFields));
	}

	private List<MiraklSchemaGroupYaml> loadCustomFieldGroups(final boolean includeKycRequiredFields) {
		return Arrays.stream(resources)
			.map(this::loadYaml)
			.filter(g -> includeKycRequiredFields || Boolean.FALSE.equals(g.getMetadata().getRequiredForKyc()))
			.toList();
	}

	private MiraklSchemaGroupYaml loadYaml(final Resource resource) {
		final Yaml yaml = new Yaml(new Constructor(MiraklSchemaGroupYaml.class, new LoaderOptions()));
		try (final InputStream is = resource.getInputStream()) {
			return yaml.load(is);
		}
		catch (final Exception e) {
			throw new HMCException("Couldn't load custom field schema from file: %s".formatted(resource.getFilename()),
					e);
		}
	}

}
