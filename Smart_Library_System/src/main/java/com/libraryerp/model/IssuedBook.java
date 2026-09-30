package com.libraryerp.model;

import java.time.LocalDate;

public record IssuedBook(int issueId, int bookId, String title,
                         LocalDate issueDate, LocalDate dueDate,
                         int renewalCount, String status) {}
