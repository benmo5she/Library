package com.storage.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.storage.exception.BookAlreadyBorrowedException;
import com.storage.exception.BookNotFoundException;
import com.storage.model.Book;
import com.storage.model.BookLoan;
import com.storage.model.User;
import com.storage.repository.BookRepository;


/**
 * Implementation of the BookService interface.
 * Provides methods for managing books in the library.
 */
@Service
public class BookService extends BaseService<Book, Long> {
	
	private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookLoanService bookLoanService;
    private final UserService userService;

    public BookService(BookRepository bookRepository, BookLoanService bookLoanService, UserService userService) {
    	super(bookRepository);
        this.bookRepository = bookRepository;        
        this.bookLoanService = bookLoanService;
        this.userService = userService;
    }

    /**
     * Adds a new book to the repository.
     *
     * @param book The book to be added.
     * @return The added book.
     */
    public Book addBook(Book book) {
        book.setBorrowed(false); // Book should be marked as "not borrowed" when newly added
        Book storedBook = bookRepository.save(book);
        logger.info("A new book was added to the repository: " + storedBook);
        return storedBook;
    }
    
    public Book findBookById(Long bookId) {
    	return bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId));
    }
    
    public Book findBookByTitle(String bookTitle) {
    	return bookRepository.findByTitle(bookTitle);
    }

    // Get all available (not borrowed) books
    public List<Book> getAvailableBooks() {
        return bookRepository.findByIsBorrowedFalse();
    }

    /**
     * Marks a book as borrowed by its ID and the user borrowing it.
     *
     * @param bookId The ID of the book to borrow.
     * @return The borrowed book.
     * @throws BookNotFoundException If the book with the given ID does not exist.
     */
    public BookLoan borrowBook(Long bookId, User user) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        if (book.isBorrowed()) {
            throw new BookAlreadyBorrowedException(bookId);
        }

        book.setBorrowed(true);
        bookRepository.save(book);

        BookLoan bookLoan = new BookLoan();
        bookLoan.setBook(book);
        if(user == null) {
        	user = userService.getDefaultUser();
        }
        bookLoan.setUser(user);
        BookLoan storedLoan = bookLoanService.loanBook(bookLoan);
        logger.info("A book was borrowed from the library: " + storedLoan);
        return storedLoan;
    }
    
    
    /**
     * Mark the book as returned, and available again to borrow.
     * @param bookId
     */
    public void returnBook(Long bookId) {
    	Book book = findBookById(bookId);
    	book.setBorrowed(false);
    	bookRepository.save(book);
    	bookLoanService.returnBook(bookId);
    }
}