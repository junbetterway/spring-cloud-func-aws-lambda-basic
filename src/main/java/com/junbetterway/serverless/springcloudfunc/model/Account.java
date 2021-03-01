package com.junbetterway.serverless.springcloudfunc.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

	private Long id;
	private String name;
	private BigDecimal balance;
	
}
