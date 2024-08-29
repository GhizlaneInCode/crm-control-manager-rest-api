package com.giantlink.project.exceptions;

public class GlForeignKeyViolation extends Exception {

	private static final long serialVersionUID = 1L;

	public GlForeignKeyViolation(String entityNameAttribut) {
		super("You can't delete " + entityNameAttribut + " foreignkey violation !");
	}
}
