package com.paypal.observability.miraklfieldschecks.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiraklFieldSchemaGroupMetadataYaml {

	private String owner;

	private String shopType;

	private String group;

	private Boolean requiredForKyc;

}
