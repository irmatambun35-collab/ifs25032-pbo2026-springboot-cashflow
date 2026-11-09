package org.delcom.ifs25032pbo2026springbootcashflow.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity inti yang merepresentasikan satu transaksi keuangan.
 * Berada di layer domain: bebas dari urusan tampilan dan penyimpanan.
 */
public class Transaction {
    /** ID unik, tidak boleh berubah setelah dibuat. */
    private final int id;
    private String description;
    private BigDecimal amount;
    private final TransactionType type;
    private final LocalDateTime createdAt;

    public Transaction(int id, String description, BigDecimal amount, TransactionType type) {
        this(id, description, amount, type, LocalDateTime.now());
    }

    public Transaction(int id, String description, BigDecimal amount,
                       TransactionType type, LocalDateTime createdAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isIncome() { return type == TransactionType.INCOME; }
    public boolean isExpense() { return type == TransactionType.EXPENSE; }

    /** Pengaruh transaksi terhadap saldo: positif untuk pemasukan, negatif untuk pengeluaran. */
    public BigDecimal signedAmount() {
        return isIncome() ? amount : amount.negate();
    }

    /** Mengubah keterangan transaksi. */
    public void changeDescription(String description) {
        this.description = description;
    }

    /** Mengubah jumlah transaksi. */
    public void changeAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
