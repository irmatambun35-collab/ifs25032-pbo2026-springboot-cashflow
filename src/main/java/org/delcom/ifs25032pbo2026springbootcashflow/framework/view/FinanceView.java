package org.delcom.ifs25032pbo2026springbootcashflow.framework.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25032pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.entity.TransactionType;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25032pbo2026springbootcashflow.usecase.FinanceUseCase;
import java.util.Comparator;
import java.util.List;

/**
 * Tampilan web Vaadin aplikasi Cashflow.
 * View hanya berinteraksi dengan use case dan presenter; exception domain
 * ditangani lewat try-catch dan ditampilkan sebagai Notification.
 */
@Route("")
@PageTitle("Cashflow")
public class FinanceView extends VerticalLayout {
    private final FinanceUseCase useCase;
    private final FinancePresenter presenter;

    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final TextField searchField = new TextField();
    private final ComboBox<SortOption> sortBox = new ComboBox<>();
    private final Paragraph statusLabel = new Paragraph();
    private final Span emptyState = new Span("- Data transaksi belum tersedia!");
    private final Span totalCount = new Span("0");
    private final Span totalIncome = new Span();
    private final Span totalExpense = new Span();
    private final Span balanceLabel = new Span();

    public FinanceView(FinanceUseCase useCase, FinancePresenter presenter) {
        this.useCase = useCase;
        this.presenter = presenter;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureGrid();
        configureToolbar();
        add(new H1("Cashflow"), buildSummaryCards(), buildToolbar(), statusLabel, grid);
        refreshAll();
    }

    // ---------- ringkasan ----------
    private HorizontalLayout buildSummaryCards() {
        HorizontalLayout summary = new HorizontalLayout(
                card("Total Transaksi", totalCount, "#DBEAFE", "#1D4ED8", "#1E40AF"),
                card("Total Pemasukan", totalIncome, "#DCFCE7", "#16A34A", "#166534"),
                card("Total Pengeluaran", totalExpense, "#FEE2E2", "#DC2626", "#991B1B"),
                card("Saldo", balanceLabel, "#FEF3C7", "#D97706", "#92400E"));
        summary.setWidthFull();
        summary.getStyle().set("align-items", "stretch");
        return summary;
    }

    private Div card(String label, Span value, String bg, String accent, String valueColor) {
        Span caption = new Span(label);
        caption.getStyle().set("display", "block").set("font-size", "0.875rem")
                .set("font-weight", "500").set("color", accent);
        value.getStyle().set("display", "block").set("margin-top", "0.35rem")
                .set("font-size", "1.5rem").set("font-weight", "700").set("color", valueColor);
        Div card = new Div(caption, value);
        card.getStyle().set("flex", "1").set("min-width", "0").set("background", bg)
                .set("border", "1px solid " + accent).set("border-left", "6px solid " + accent)
                .set("border-radius", "12px").set("padding", "1rem 1.1rem")
                .set("box-sizing", "border-box");
        return card;
    }

    private void updateSummary() {
        totalCount.setText(String.valueOf(useCase.getAllTransactions().size()));
        totalIncome.setText(presenter.formatMoney(useCase.getTotalIncome()));
        totalExpense.setText(presenter.formatMoney(useCase.getTotalExpense()));
        balanceLabel.setText(presenter.formatBalance(useCase.getBalance()).replaceFirst("^Saldo: ", ""));
    }

    // ---------- grid ----------
    private void configureGrid() {
        grid.addColumn(Transaction::getId).setHeader("ID").setKey("id")
                .setAutoWidth(true).setFlexGrow(0).setSortable(true).setComparator(Transaction::getId);
        grid.addColumn(Transaction::getDescription).setHeader("Keterangan").setKey("description")
                .setFlexGrow(1).setSortable(true)
                .setComparator(Comparator.comparing(Transaction::getDescription, String.CASE_INSENSITIVE_ORDER));
        grid.addColumn(t -> presenter.formatMoney(t.getAmount())).setHeader("Jumlah").setKey("amount")
                .setAutoWidth(true).setSortable(true).setComparator(Transaction::getAmount);
        grid.addColumn(t -> presenter.formatType(t.getType())).setHeader("Tipe").setKey("type")
                .setAutoWidth(true).setSortable(true).setComparator(Comparator.comparing(Transaction::getType));
        grid.addColumn(presenter::formatCreatedAt).setHeader("Waktu").setKey("createdAt")
                .setAutoWidth(true).setSortable(true).setComparator(Comparator.comparing(Transaction::getCreatedAt));
        grid.addComponentColumn(this::actionButtons).setHeader("Tindakan").setKey("tindakan")
                .setAutoWidth(true).setFlexGrow(0).setSortable(false);
        grid.setEmptyStateComponent(emptyState);
        grid.setSizeFull();
    }

    private HorizontalLayout actionButtons(Transaction t) {
        Button delete = new Button("Hapus", VaadinIcon.TRASH.create(), e -> openDeleteDialog(t));
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY_INLINE);
        return new HorizontalLayout(delete);
    }

    // ---------- toolbar ----------
    private void configureToolbar() {
        searchField.setPlaceholder("Cari keterangan...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);

        sortBox.setPlaceholder("Urutkan...");
        sortBox.setItems(SortOption.values());
        sortBox.setItemLabelGenerator(SortOption::label);
        sortBox.setClearButtonVisible(true);
        sortBox.addValueChangeListener(e -> applySort(e.getValue()));
    }

    private HorizontalLayout buildToolbar() {
        Button search = new Button("Cari", VaadinIcon.SEARCH.create(), e -> searchTransactions());
        Button refresh = new Button("Muat Ulang", VaadinIcon.REFRESH.create(), e -> refreshAll());
        Button income = new Button("Tambah Pemasukan", VaadinIcon.PLUS.create(),
                e -> openAddDialog(TransactionType.INCOME));
        income.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        Button expense = new Button("Tambah Pengeluaran", VaadinIcon.MINUS.create(),
                e -> openAddDialog(TransactionType.EXPENSE));
        expense.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        HorizontalLayout toolbar = new HorizontalLayout(searchField, search, sortBox, refresh, income, expense);
        toolbar.setWidthFull();
        toolbar.setFlexGrow(1, searchField);
        return toolbar;
    }

    // ---------- dialog ----------
    private void openAddDialog(TransactionType type) {
        boolean isIncome = type == TransactionType.INCOME;
        TextField description = new TextField("Keterangan");
        description.setWidthFull();
        TextField amount = new TextField("Jumlah (Rp)");
        amount.setWidthFull();

        Dialog dialog = new Dialog();
        Button save = new Button("Simpan", VaadinIcon.CHECK.create(), e -> {
            try {
                Transaction created = isIncome
                        ? useCase.addIncome(description.getValue(), amount.getValue())
                        : useCase.addExpense(description.getValue(), amount.getValue());
                showSuccess(presenter.showAddSuccess(created));
                dialog.close();
                refreshAll();
            } catch (ValidationException ex) {
                showError(presenter.showValidationError(ex.getMessage()));
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());

        VerticalLayout form = new VerticalLayout(description, amount);
        form.setPadding(false);
        buildDialog(dialog, isIncome ? "Tambah Pemasukan" : "Tambah Pengeluaran",
                isIncome ? "#16A34A" : "#DC2626", form, cancel, save);
        dialog.open();
    }

    private void openDeleteDialog(Transaction t) {
        Paragraph message = new Paragraph("Yakin ingin menghapus transaksi \"" + t.getDescription()
                + "\" (ID " + t.getId() + ")?");
        message.getStyle().set("margin", "0");
        Dialog dialog = new Dialog();
        Button confirm = new Button("Hapus", VaadinIcon.TRASH.create(), e -> {
            deleteTransaction(t);
            dialog.close();
        });
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        Button cancel = new Button("Batal", VaadinIcon.CLOSE.create(), e -> dialog.close());
        buildDialog(dialog, "Menghapus Transaksi", "#DC2626", message, cancel, confirm);
        dialog.open();
    }

    private void buildDialog(Dialog dialog, String title, String headerColor,
                             com.vaadin.flow.component.Component content, Button... footerButtons) {
        dialog.setWidth("480px");
        dialog.setMaxWidth("95vw");
        dialog.setResizable(false);

        Span headerTitle = new Span(title);
        headerTitle.getStyle().set("color", "#FFFFFF").set("font-size", "1.125rem").set("font-weight", "600");
        Div header = new Div(headerTitle);
        header.getStyle().set("background", headerColor).set("padding", "1rem 1.5rem");

        Div body = new Div(content);
        body.getStyle().set("padding", "1.25rem 1.5rem");

        Div footer = new Div();
        footer.getStyle().set("display", "flex").set("justify-content", "flex-end").set("gap", "0.75rem")
                .set("background", "#F3F4F6").set("padding", "0.85rem 1.5rem");
        footer.add(footerButtons);

        dialog.add(new Div(header, body, footer));
    }

    // ---------- aksi ----------
    private void deleteTransaction(Transaction t) {
        try {
            useCase.removeTransaction(t.getId());
            showSuccess(presenter.showRemoveSuccess());
            refreshAll();
        } catch (EntityNotFoundException ex) {
            showError(presenter.showRemoveFailed(ex.getId()));
        }
    }

    private void searchTransactions() {
        String keyword = searchField.getValue();
        if (keyword == null || keyword.isBlank()) {
            refreshAll();
            return;
        }
        List<Transaction> results = useCase.searchTransactions(keyword);
        emptyState.setText("- Transaksi tidak ditemukan!");
        refreshGrid(results, "Hasil Pencarian: \"" + keyword + "\"");
    }

    private void applySort(SortOption option) {
        if (option == null) {
            return;
        }
        emptyState.setText("- Data transaksi belum tersedia!");
        refreshGrid(useCase.sortTransactions(option), "Daftar Transaksi (" + option.label() + ")");
    }

    private void refreshAll() {
        emptyState.setText("- Data transaksi belum tersedia!");
        refreshGrid(useCase.getAllTransactions(), "Daftar Transaksi");
        searchField.clear();
    }

    private void refreshGrid(List<Transaction> list, String title) {
        grid.setItems(list);
        updateSummary();
        statusLabel.setText(list.isEmpty()
                ? title + " — " + emptyState.getText().replaceFirst("^- ", "")
                : title);
    }

    private void showSuccess(String message) {
        Notification n = Notification.show(message, 3000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showError(String message) {
        Notification n = Notification.show(message, 4000, Notification.Position.BOTTOM_START);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
