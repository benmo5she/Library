package com.storage.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.storage.exception.LoanNotFoundException;
import com.storage.model.BookLoan;
import com.storage.repository.BookLoanRepository;

/**
 * Service for managing book loans and tracking overdue books.
 */
@Service
public class BookLoanService extends BaseService<BookLoan, Long> {

    private static final Logger logger = LoggerFactory.getLogger(BookLoanService.class);

    private final BookLoanRepository bookLoanRepository;

    @Value("${library.loan.max-duration-days:3}")
    private int maxLoanDurationDays;

    public BookLoanService(BookLoanRepository bookLoanRepository) {
        super(bookLoanRepository);
        this.bookLoanRepository = bookLoanRepository;
    }

    public BookLoan loanBook(BookLoan bookLoan) {
        bookLoan.setLoanDate(LocalDateTime.now());
        bookLoan.setDueDate(LocalDateTime.now().plusDays(maxLoanDurationDays));
        return bookLoanRepository.save(bookLoan);
    }

    public void returnBook(Long loanId) {
        BookLoan loan = bookLoanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));
        loan.setReturnDate(LocalDateTime.now());
        bookLoanRepository.save(loan);
    }

    /**
     * Check the repository for books loans that are overdue
     * It will send alert containing the book and the user who is currently borrowing it. 
     */
    @Scheduled(cron = "${book.loan.scheduler.cron:0 0 0 * * *}")
    public void checkOverdueLoans() {
        List<BookLoan> loans = bookLoanRepository.findByReturnDateIsNull();
        LocalDateTime now = LocalDateTime.now();

        loans.forEach(loan -> {
            if (loan.getDueDate().isBefore(now)) {
                logger.info("Book '{}' loaned by user '{}' is overdue!",
                        loan.getBook().getTitle(), loan.getUser().getUsername());
            }
        });
    }
}