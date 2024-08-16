package com.storage.controller;

import java.util.List;

import javax.naming.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.storage.model.Book;
import com.storage.model.BookLoan;
import com.storage.model.User;
import com.storage.service.BookService;
import com.storage.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/books")
public class BookController {

	private BookService bookService;

	private final UserService userService;
	
	public BookController(BookService bookService, UserService userService) {
		this.bookService = bookService;
		this.userService = userService;
	}
	
    @ModelAttribute
    public void beforeEveryRequest(HttpServletRequest request) throws AuthenticationException {
    	userService.authenticateBasic(request);
    }

	@PostMapping("/addBook")
    public ResponseEntity<Book> addBook(@RequestBody Book book, HttpServletRequest request) {		
        Book newBook = bookService.addBook(book);
        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }
	
	@GetMapping("/{bookId}")
	public ResponseEntity<Book> getBookById(@PathVariable Long bookId, HttpServletRequest request) {
		return new ResponseEntity<>(bookService.findBookById(bookId), HttpStatus.OK);
	}

	@GetMapping("/available")
	public ResponseEntity<List<Book>> getAllAvailableBooks(HttpServletRequest request) {
		List<Book> availableBooks = bookService.getAvailableBooks();
		return new ResponseEntity<>(availableBooks, HttpStatus.OK);
	}

	@GetMapping("/borrow/{bookId}")
	public ResponseEntity<BookLoan> borrowBook(@PathVariable Long bookId, 
			HttpServletRequest request) throws AuthenticationException {
		User user = userService.authenticateBasic(request);
		BookLoan loan = bookService.borrowBook(bookId, user);
		return new ResponseEntity<>(loan, HttpStatus.OK);
	}
}