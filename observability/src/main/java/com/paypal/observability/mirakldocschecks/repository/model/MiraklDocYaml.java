package com.paypal.observability.mirakldocschecks.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiraklDocYaml {

	private String label;

	private String code;

	private String description;

}
