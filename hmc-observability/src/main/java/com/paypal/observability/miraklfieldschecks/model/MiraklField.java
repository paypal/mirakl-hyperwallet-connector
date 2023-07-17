package com.paypal.observability.miraklfieldschecks.model;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class MiraklField implements MiraklSchemaItem {

	private String label;

	private String code;

	private String description;

	private MiraklFieldType type;

	private MiraklFieldPermissions permissions;

	@Builder.Default
	private Boolean required = Boolean.FALSE;

	private String regexpPattern;

	@Builder.Default
	private List<String> allowedValues = new ArrayList<>();

}
