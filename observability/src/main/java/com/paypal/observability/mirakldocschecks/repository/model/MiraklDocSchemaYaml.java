package com.paypal.observability.mirakldocschecks.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiraklDocSchemaYaml {

	private List<MiraklDocYaml> documents;

}
