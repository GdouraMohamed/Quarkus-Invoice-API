package org.acme.api;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public class CreateInvoiceRequest {

    @NotBlank
    @Size(max = 120)
    public String customerName;

    @Email
    @Size(max = 180)
    public String customerEmail;

    @NotBlank
    @Size(min = 3, max = 3)
    public String currency;

    @NotNull
    public LocalDate issueDate;

    @NotNull
    public LocalDate dueDate;

    @NotEmpty
    public List<@Valid Line> lines;

    public static class Line {
        @NotBlank
        @Size(max = 300)
        public String description;

        @Min(1)
        public int quantity;

        @Min(0)
        public long unitPriceCents;
    }
}

