package com.junbetterway.serverless.springcloudfunc.functions;

import java.util.function.Function;

import com.junbetterway.serverless.springcloudfunc.model.Account;

public class CreateAccount implements Function<Account, Account> {
	
	@Override
	public Account apply(final Account request) {
				
		Account newAccount = Account.builder()
				.id(Long.valueOf(1))
				.name(request.getName())
				.balance(request.getBalance())
				.build();
		
		return newAccount;
		
	}

}
