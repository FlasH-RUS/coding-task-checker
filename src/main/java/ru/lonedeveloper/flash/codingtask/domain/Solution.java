package ru.lonedeveloper.flash.codingtask.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.entity.RooJpaEntity;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaEntity
public class Solution {

	/**
	 */
	@NotNull
	@ManyToOne
	private Task task;

	/**
	 */
	@Column(nullable = false, length = 15)
	private String ip;

	/**
	 */
	@NotNull
	@Size(min = 1, max = 30)
	private String author;

	/**
	 */
	@NotNull
	@Size(min = 10, max = 4000)
	private String code;

	/**
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(style = "-M")
	private Date createdAt;

	/**
	 */
	@Column
	private Boolean successful;

	/**
	 */
	@Column(length = 50)
	private String results;

	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
	}

}
