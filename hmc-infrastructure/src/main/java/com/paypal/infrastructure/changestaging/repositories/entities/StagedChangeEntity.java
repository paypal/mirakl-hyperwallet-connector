package com.paypal.infrastructure.changestaging.repositories.entities;

import com.paypal.infrastructure.changestaging.model.ChangeOperation;
import com.paypal.infrastructure.changestaging.model.ChangeTarget;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class StagedChangeEntity {

	@Id
	private String id;

	@NotNull
	private String type;

	@NotNull
	private ChangeOperation operation;

	@NotNull
	private ChangeTarget target;

	@NotNull
	@Column(length = 32768)
	private String payload;

	@NotNull
	private Date creationDate;

}
