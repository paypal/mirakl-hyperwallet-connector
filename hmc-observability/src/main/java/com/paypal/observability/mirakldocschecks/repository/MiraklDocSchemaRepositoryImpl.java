package com.paypal.observability.mirakldocschecks.repository;

import com.paypal.infrastructure.support.exceptions.HMCException;
import com.paypal.observability.mirakldocschecks.repository.model.MiraklDocSchemaYaml;
import com.paypal.observability.mirakldocschecks.repository.model.MiraklDocYaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class MiraklDocSchemaRepositoryImpl implements MiraklDocSchemaRepository {

	@Value("classpath:mirakl/docs-schemas/*")
	private Resource[] resources;

	@Override
	public MiraklDocSchemaYaml loadCustomFieldsSchema() {
		return new MiraklDocSchemaYaml(loadDocuments());
	}

	private List<MiraklDocYaml> loadDocuments() {
		//@formatter:off
		return Arrays.stream(resources)
				.map(this::loadYaml)
				.map(MiraklDocSchemaYaml::getDocuments)
				.flatMap(Collection::stream)
				.toList();
		//@formatter:on
	}

	private MiraklDocSchemaYaml loadYaml(final Resource resource) {
		final Yaml yaml = new Yaml(new Constructor(MiraklDocSchemaYaml.class, new LoaderOptions()));
		try (final InputStream is = resource.getInputStream()) {
			return yaml.load(is);
		}
		catch (final Exception e) {
			throw new HMCException("Couldn't load custom field schema from file: %s".formatted(resource.getFilename()),
					e);
		}
	}

}
