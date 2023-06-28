package com.paypal.infrastructure.mirakl.settings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiraklClientSettings {

	private boolean stageChanges = false;

}
