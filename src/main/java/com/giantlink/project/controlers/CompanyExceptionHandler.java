package com.giantlink.project.controlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.giantlink.project.exceptions.GlAlreadyExistException;
import com.giantlink.project.exceptions.GlNotFoundException;

@RestControllerAdvice
public class CompanyExceptionHandler {

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(GlAlreadyExistException.class)
	public Map<String, String> checkEntityNameAttribut(GlAlreadyExistException exception) {
		Map<String, String> errors = new HashMap<String, String>();
		errors.put("ALREADY_EXISTS", exception.getMessage());
		return errors;
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(GlNotFoundException.class)
	public Map<String, String> checkEntityNameAttribut(GlNotFoundException exception) {
		Map<String, String> errors = new HashMap<String, String>();
		errors.put("NOT_FOUND", exception.getMessage());
		return errors;
	}

}
