package com.storage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Base for implementation for the various services in the application.
 *  
 * @param <T> Type of entity to be serviced by the class
 * @param <ID> Type of the id of the entity
 */
public abstract class BaseService<T, ID> {

	private final JpaRepository<T, ID> repository;
	
	public BaseService(JpaRepository<T, ID> repository) {
		this.repository = repository;
	}	   

    public T add(T entity) {
        return repository.save(entity);
    }

    public Optional<T> getById(ID id) {
        return repository.findById(id);
    }

    public List<T> getAll() {
        return repository.findAll();
    }

    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    protected JpaRepository<T, ID> getRepository() {
        return repository;
    }
	

}
