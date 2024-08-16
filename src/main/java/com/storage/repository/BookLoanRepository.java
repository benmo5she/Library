package com.storage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.storage.model.BookLoan;

@Repository
public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

	List<BookLoan> findByReturnDateIsNull();
}