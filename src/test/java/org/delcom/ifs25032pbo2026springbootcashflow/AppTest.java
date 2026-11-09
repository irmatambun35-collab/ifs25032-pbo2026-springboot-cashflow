package org.delcom.ifs25032pbo2026springbootcashflow;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.delcom.ifs25032pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25032pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25032pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Smoke test: konteks Spring naik dan wiring Clean Architecture terpasang. */
@SpringBootTest
class AppTest {
    @Autowired private ITransactionRepository repository;
    @Autowired private FinanceUseCase useCase;
    @Autowired private FinancePresenter presenter;

    @Test
    void contextLoadsAndBeansAreWired() {
        assertNotNull(repository);
        assertNotNull(useCase);
        assertNotNull(presenter);
    }
}
