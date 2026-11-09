package org.delcom.ifs25032pbo2026springbootcashflow.adapter.presenter;

import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.TransactionType;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** Memformat data domain menjadi teks siap tampil. */
public class FinancePresenter {
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** 1000000 -> "Rp 1,000,000.00". */
    public String formatMoney(BigDecimal amount) {
        return "Rp " + String.format(Locale.US, "%,.2f", amount);
    }

    public String formatType(TransactionType type) {
        return type == TransactionType.INCOME ? "Pemasukan" : "Pengeluaran";
    }

    public String formatCreatedAt(Transaction t) {
        return t.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** [id] keterangan | Rp 1,000,000.00 | [Pemasukan] */
    public String format(Transaction t) {
        return String.format("[%d] %s | %s | [%s]",
                t.getId(), t.getDescription(), formatMoney(t.getAmount()), formatType(t.getType()));
    }

    public String formatBalance(BigDecimal balance) {
        return "Saldo: " + formatMoney(balance);
    }

    public String formatList(List<Transaction> list, String header, String emptyMessage) {
        StringBuilder sb = new StringBuilder(header);
        if (list.isEmpty()) {
            return sb.append('\n').append(emptyMessage).toString();
        }
        return sb.append('\n')
                .append(list.stream().map(this::format).collect(Collectors.joining("\n")))
                .toString();
    }

    public String showTransactions(List<Transaction> list) {
        return formatList(list, "Daftar Transaksi:", "- Data transaksi belum tersedia!");
    }

    public String showSearchResults(List<Transaction> list, String keyword) {
        return formatList(list, "Hasil Pencarian: \"" + keyword + "\"", "- Transaksi tidak ditemukan!");
    }

    public String showSortedTransactions(List<Transaction> list) {
        return formatList(list, "Daftar Transaksi (Terurut):", "- Data transaksi belum tersedia!");
    }

    public String showAddSuccess(Transaction t) { return "Berhasil menambah transaksi: " + format(t); }

    public String showRemoveSuccess() { return "Berhasil menghapus transaksi."; }

    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    public String showInvalidChoice() { return "[!] Pilihan tidak dimengerti."; }

    public String showInvalidId() { return "[!] ID tidak valid!"; }

    public String showInvalidSortOption() { return "[!] Pilihan tidak valid!"; }

    public String showValidationError(String message) { return "[!] " + message; }
}
