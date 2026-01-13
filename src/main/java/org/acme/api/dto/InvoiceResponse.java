package org.acme.api.dto;

import org.acme.domain.Invoice;
import org.acme.domain.InvoiceLine;
import org.acme.domain.InvoiceStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class InvoiceResponse {

    public Long id;
    public String customerName;
    public String customerEmail;
    public InvoiceStatus status;
    public String currency;
    public LocalDate issueDate;
    public LocalDate dueDate;
    public long totalCents;
    public long version;
    public Instant createdAt;
    public Instant updatedAt;
    public List<Line> lines;

    public static class Line {
        public Long id;
        public String description;
        public int quantity;
        public long unitPriceCents;
        public long lineTotalCents;
    }

    // ✅ THIS IS THE IMPORTANT PART
    public static InvoiceResponse from(Invoice invoice) {
        InvoiceResponse r = new InvoiceResponse();

        r.id = invoice.id;
        r.customerName = invoice.customerName;
        r.customerEmail = invoice.customerEmail;
        r.status = invoice.status;
        r.currency = invoice.currency;
        r.issueDate = invoice.issueDate;
        r.dueDate = invoice.dueDate;
        r.totalCents = invoice.totalCents;
        r.version = invoice.version;
        r.createdAt = invoice.createdAt;
        r.updatedAt = invoice.updatedAt;

        r.lines = invoice.lines.stream().map(InvoiceResponse::mapLine).toList();

        return r;
    }

    private static Line mapLine(InvoiceLine l) {
        Line lr = new Line();
        lr.id = l.id;
        lr.description = l.description;
        lr.quantity = l.quantity;
        lr.unitPriceCents = l.unitPriceCents;
        lr.lineTotalCents = l.lineTotalCents;
        return lr;
    }
}
