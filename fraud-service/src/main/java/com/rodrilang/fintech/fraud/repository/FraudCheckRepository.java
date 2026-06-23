package com.rodrilang.fintech.fraud.repository;

import com.rodrilang.fintech.fraud.model.FraudCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudCheckRepository extends JpaRepository<FraudCheck, Long> {
}