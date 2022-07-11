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
public class MiraklFieldYaml {

	private String label;

	private String code;

	private String description;

	private MiraklFieldTypeYaml type;

	private MiraklFieldPermissionsYaml permissions;

	private Boolean required;

	private String regexpPattern;

	private List<String> allowedValues;

}
