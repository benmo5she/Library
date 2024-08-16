package com.storage.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book loan entity for the repository, it represents book loans made through the system.
 * It records both when the book is borrowed and returned (return date)
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookLoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    //@JsonFormat(shape = Shape.STRING)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loanDate;

    @Column(nullable = false)
    //@JsonFormat(shape = Shape.STRING)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;
    
    @Column
    //@JsonFormat(shape = Shape.STRING)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnDate;

}
