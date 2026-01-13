package org.acme.repo;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.Invoice;
@ApplicationScoped
public class InvoiceRepository implements PanacheRepository<Invoice> {
}
