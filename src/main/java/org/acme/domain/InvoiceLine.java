package org.acme.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "invoice_lines")
public class InvoiceLine extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    public Invoice invoice;

    @NotBlank
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    public String description;

    @Min(1)
    @Column(nullable = false)
    public int quantity;

    @Min(0)
    @Column(name = "unit_price_cents", nullable = false)
    public long unitPriceCents;

    @Min(0)
    @Column(name = "line_total_cents", nullable = false)
    public long lineTotalCents;
}

