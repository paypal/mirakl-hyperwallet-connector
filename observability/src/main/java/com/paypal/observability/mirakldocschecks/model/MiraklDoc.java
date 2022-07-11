package com.paypal.observability.mirakldocschecks.model;

import com.paypal.observability.miraklschemadiffs.model.MiraklSchemaItem;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MiraklDoc implements MiraklSchemaItem {

	private String code;

	private String label;

	private String description;

}
