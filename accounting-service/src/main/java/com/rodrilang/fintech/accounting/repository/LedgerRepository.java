package com.rodrilang.fintech.accounting.repository;

import com.rodrilang.fintech.accounting.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<LedgerEntry, String> {
}
