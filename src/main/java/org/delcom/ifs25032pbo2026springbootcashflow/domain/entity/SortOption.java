package org.delcom.ifs25032pbo2026springbootcashflow.domain.entity;

import java.util.Comparator;

/**
 * Kriteria pengurutan transaksi. Setiap opsi membawa comparator-nya sendiri
 * sehingga aturan urutan terpusat di domain.
 */
public enum SortOption {
    AMOUNT_ASC("Jumlah terkecil", Comparator.comparing(Transaction::getAmount)),
    AMOUNT_DESC("Jumlah terbesar", Comparator.comparing(Transaction::getAmount).reversed()),
    /** INCOME dideklarasikan lebih dulu di enum, sehingga urutan natural = pemasukan dulu. */
    INCOME_FIRST("Pemasukan dulu", Comparator.comparing(Transaction::getType)),
    EXPENSE_FIRST("Pengeluaran dulu", Comparator.comparing(Transaction::getType).reversed());

    private final String label;
    private final Comparator<Transaction> comparator;

    SortOption(String label, Comparator<Transaction> comparator) {
        this.label = label;
        this.comparator = comparator;
    }

    public String label() { return label; }

    public Comparator<Transaction> comparator() { return comparator; }
}
