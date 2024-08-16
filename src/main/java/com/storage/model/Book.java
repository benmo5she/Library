package com.storage.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Book entity in the repository, it includes data about the book and its borrow status.
 */
@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String author;
	private String genere;
	private boolean isBorrowed;

	public Book(String title, String author) {
		this.title = title;
		this.author = author;
		this.isBorrowed = false; // Default to false
	}
}