package org.acme.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoice extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @NotBlank
    @Size(max = 120)
    @Column(name = "customer_name", nullable = false, length = 120)
    public String customerName;

    @Email
    @Size(max = 180)
    @Column(name = "customer_email", length = 180)
    public String customerEmail;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public InvoiceStatus status;

    @NotBlank
    @Size(min = 3, max = 3)
    @Column(nullable = false, length = 3)
    public String currency;

    @NotNull
    @Column(name = "issue_date", nullable = false)
    public LocalDate issueDate;

    @NotNull
    @Column(name = "due_date", nullable = false)
    public LocalDate dueDate;

    @Min(0)
    @Column(name = "total_cents", nullable = false)
    public long totalCents;

    @Version
    public long version;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<InvoiceLine> lines = new ArrayList<>();

    @PrePersist
    void onCreate() {
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (status == null) status = InvoiceStatus.DRAFT;
        if (currency == null) currency = "EUR";
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
