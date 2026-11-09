package org.delcom.ifs25032pbo2026springbootcashflow.usecase;

import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import java.math.BigDecimal;
import java.util.List;

/**
 * Logika bisnis pencatatan keuangan. Tanpa I/O UI: hanya memproses data
 * dan melempar checked exception domain bila gagal.
 */
public class FinanceUseCase {
    private final ITransactionRepository repository;

    public FinanceUseCase(ITransactionRepository repository) {
        this.repository = repository;
    }

    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    public Transaction addIncome(String description, String amountText) throws ValidationException {
        String desc = validateDescription(description);
        return repository.saveIncome(desc, parseAmount(amountText));
    }

    public Transaction addExpense(String description, String amountText) throws ValidationException {
        String desc = validateDescription(description);
        return repository.saveExpense(desc, parseAmount(amountText));
    }

    public void removeTransaction(int id) throws EntityNotFoundException {
        if (!repository.deleteById(id)) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Mencari transaksi yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(String keyword) {
        String lower = keyword == null ? "" : keyword.trim().toLowerCase();
        return repository.findBy(t -> t.getDescription().toLowerCase().contains(lower));
    }

    public List<Transaction> sortTransactions(SortOption option) {
        return repository.findAll().stream().sorted(option.comparator()).toList();
    }

    /** Saldo = total pemasukan - total pengeluaran. */
    public BigDecimal getBalance() {
        return repository.findAll().stream()
                .map(Transaction::signedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalIncome() {
        return repository.findAll().stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense() {
        return repository.findAll().stream()
                .filter(Transaction::isExpense)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String validateDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
        return description.trim();
    }

    private BigDecimal parseAmount(String amountText) throws ValidationException {
        if (amountText == null || amountText.trim().isEmpty()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountText.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException("Jumlah harus berupa angka");
        }
        if (amount.signum() <= 0) {
            throw new ValidationException("Jumlah harus lebih besar dari 0");
        }
        return amount;
    }
}
