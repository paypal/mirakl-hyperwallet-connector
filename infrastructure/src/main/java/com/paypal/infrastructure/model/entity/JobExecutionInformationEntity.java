package com.paypal.infrastructure.model.entity;

import com.paypal.infrastructure.model.job.JobStatus;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
