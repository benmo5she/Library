package com.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.storage.model.Book;
import com.storage.model.BookLoan;
import com.storage.model.User;
import com.storage.repository.BookLoanRepository;
import com.storage.repository.BookRepository;
import com.storage.repository.UserRepository;

/**
 * Integration tests for the controller, 
 * sending generic requests the controller and asserting the response. 
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
//@DirtiesContext
public class BookControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private BookLoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;
    
    private final Random random = new Random();

    @BeforeEach
    void setUp() {
    	loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    private User dummyUser() {
        User user = new User();
        user.setUsername("admin");
        return user;
    }

    @Test
    void testAddBook() throws Exception {
        // Arrange
        URI uri = new URI("http://localhost:" + port + "/books/addBook");
        User user = dummyUser();
        User storedUser = userRepository.save(user);
        Book book = new Book("New Book", "Author");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(storedUser.getUsername(), "password");
        HttpEntity<Book> request = new HttpEntity<>(book, headers);

        // Act
        ResponseEntity<Book> response = restTemplate.postForEntity(uri, request, Book.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId(), "Book ID should not be null after creation");
    }

    @Test
    void testGetAllAvailableBooks() throws Exception {
        // Arrange
        URI uri = new URI("http://localhost:" + port + "/books/available");
        bookRepository.save(new Book("Available Book 1", "Author 1"));
        bookRepository.save(new Book("Available Book 2", "Author 2"));

        // Act
        ResponseEntity<Book[]> response = restTemplate.getForEntity(uri, Book[].class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length, "Should return 2 available books");
    }

    @Test
    void testBorrowBook() throws Exception {
        // Arrange
        Book book = bookRepository.save(new Book("Borrowable Book", "Author"));
        URI uri = new URI("http://localhost:" + port + "/books/borrow/" + book.getId());
        userRepository.save(dummyUser());
        // Act        
        ResponseEntity<BookLoan> response = restTemplate.getForEntity(uri, BookLoan.class);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getLoanDate(), "The book should be marked as borrowed");
    }

    @Test
    void testBorrowNonExistentBook() throws Exception {
        // Arrange
    	int randomId = random.nextInt(Integer.MAX_VALUE) + 1;
        URI uri = new URI("http://localhost:" + port + "/books/borrow/" + randomId);
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Book not found with ID: " + randomId));
    }
}