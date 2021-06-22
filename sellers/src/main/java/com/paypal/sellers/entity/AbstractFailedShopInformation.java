package com.paypal.sellers.entity;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Inheritance
@Data
@SuperBuilder
public abstract class AbstractFailedShopInformation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;

	protected String shopId;

	protected AbstractFailedShopInformation() {
	}

}
