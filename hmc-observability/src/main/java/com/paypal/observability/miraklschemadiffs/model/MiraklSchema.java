package com.paypal.observability.miraklschemadiffs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@Builder
public class MiraklSchema {

	private List<MiraklSchemaItem> items;

	private Class<? extends MiraklSchemaItem> type;

}
