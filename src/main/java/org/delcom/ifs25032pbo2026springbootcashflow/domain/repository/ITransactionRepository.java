package org.delcom.ifs25032pbo2026springbootcashflow.domain.repository;

import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.Transaction;
import java.math.BigDecimal;

/** Port spesifik Transaction dengan pembuatan ID otomatis. */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    /** Menyimpan pemasukan baru dan mengembalikannya lengkap dengan ID. */
    Transaction saveIncome(String description, BigDecimal amount);

    /** Menyimpan pengeluaran baru dan mengembalikannya lengkap dengan ID. */
    Transaction saveExpense(String description, BigDecimal amount);
}
