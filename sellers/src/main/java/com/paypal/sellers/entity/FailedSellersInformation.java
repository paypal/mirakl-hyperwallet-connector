package com.paypal.sellers.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Class to hold delta execution information of any {@code job}
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class FailedSellersInformation extends AbstractFailedShopInformation implements Serializable {

	public FailedSellersInformation() {
		super();
	}

}
