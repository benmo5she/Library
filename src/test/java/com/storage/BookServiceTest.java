package com.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.storage.exception.BookNotFoundException;
import com.storage.model.Book;
import com.storage.model.BookLoan;
import com.storage.repository.BookRepository;
import com.storage.service.BookService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test the service 
 */
@SpringBootTest
@ActiveProfiles("test")
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;
    
    private final Book dummyBook = new Book("Test Title", "Test Author", "Comedy");

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();  // Clear the database before each test
    }

    @Test
    @Transactional
    void testAddBook() {
        // Arrange
        Book book = dummyBook;

        // Act
        Book savedBook = bookService.addBook(book);

        // Assert
        assertNotNull(savedBook.getId(), "Book ID should not be null after saving");
        assertEquals("Test Title", savedBook.getTitle(), "Book title should match");
        assertEquals("Test Author", savedBook.getAuthor(), "Book author should match");
        assertFalse(savedBook.isBorrowed(), "Newly added book should not be borrowed");
    }

    @Test
    @Transactional
    void testGetBookById() {
        // Arrange
        Book book = dummyBook;
        Book savedBook = bookRepository.save(book);

        // Act
        Book fetchedBook = bookService.findBookById(savedBook.getId());

        // Assert
        assertEquals(savedBook.getId(), fetchedBook.getId(), "Fetched book ID should match saved book ID");
        assertEquals(savedBook.getTitle(), fetchedBook.getTitle(), "Fetched book title should match");
    }

    @Test
    @Transactional
    void testGetAllAvailableBooks() {
        // Arrange
        Book book1 = new Book("Available Book 1", "Author 1", "Comedy");
        Book book2 = new Book("Available Book 2", "Author 2", "Horror");
        bookRepository.save(book1);
        bookRepository.save(book2);

        // Act
        List<Book> availableBooks = bookService.getAvailableBooks();

        // Assert
        assertEquals(2, availableBooks.size(), "There should be 2 available books");
        assertTrue(availableBooks.stream().noneMatch(Book::isBorrowed), "All available books should not be borrowed");
    }

    @Test
    @Transactional
    void testBorrowBook() {
        // Arrange
        Book book = dummyBook;
        Book savedBook = bookRepository.save(book);

        // Act
        BookLoan loan = bookService.borrowBook(savedBook.getId(), null);

        // Assert
        assertNull(loan.getReturnDate(), "Borrowed book should not be marked as returned");
    }

    @Test
    @Transactional
    void testBorrowNonExistentBook() {
        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> {
            bookService.borrowBook(999L, null);  // ID that doesn't exist
        }, "Should throw ResourceNotFoundException when borrowing a non-existent book");
    }
}