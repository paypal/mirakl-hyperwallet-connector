package com.paypal.jobsystem.quartzintegration.repositories.entities;

import lombok.Data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * Class to hold delta execution information of any {@code job}
 */
@Entity
@Data
public class JobExecutionInformationEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Date startTime;

	private Date endTime;

	private JobStatus status;

	private String type;

	private String name;

}
