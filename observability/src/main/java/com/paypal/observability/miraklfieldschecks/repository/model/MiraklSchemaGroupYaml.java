package com.paypal.observability.miraklfieldschecks.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiraklSchemaGroupYaml {

	private MiraklFieldSchemaGroupMetadataYaml metadata;

	private String label;

	private String description;

	private List<MiraklFieldYaml> fields;

}
