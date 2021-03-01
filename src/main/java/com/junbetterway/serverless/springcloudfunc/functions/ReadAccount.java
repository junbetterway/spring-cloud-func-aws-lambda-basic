package com.junbetterway.serverless.springcloudfunc.functions;

import java.math.BigDecimal;
import java.util.function.Supplier;

import com.junbetterway.serverless.springcloudfunc.model.Account;

public class ReadAccount implements Supplier<Account> {
	
	@Override	
	public Account get() {
		return Account.builder()
				.id(Long.valueOf(1))
				.name("Jun King Minon")
				.balance(new BigDecimal(15000))
				.build();
	}
	
}
