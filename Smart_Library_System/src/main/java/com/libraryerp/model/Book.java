package com.libraryerp.model;

public record Book(int id, String title, String author, String isbn,
                   String departmentCode, String departmentName,
                   String rack, String shelf, int totalCopies, int availableCopies) {}
