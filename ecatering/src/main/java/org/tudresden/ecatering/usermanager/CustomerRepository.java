package org.tudresden.ecatering.usermanager;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<CustomerAccount, Long>{
	Iterable<CustomerAccount> findByExpirationDate(Date expirationDate);
	Iterable<CustomerAccount> findByBusinessIdentifier(String businessIdentifier);
}
