package org.acme.api;

import org.acme.api.dto.InvoiceResponse;
import org.acme.domain.Invoice;
import org.acme.domain.InvoiceLine;
import org.acme.domain.InvoiceStatus;
import org.acme.repo.InvoiceRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.util.List;

@Path("/api/invoices")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvoiceResource {

    private final InvoiceRepository repo;

    public InvoiceResource(InvoiceRepository repo) {
        this.repo = repo;
    }

    /* ===== LIST ===== */

    @GET
    @Retry(maxRetries = 2, delay = 300)
    @Timeout(2000)
    @Fallback(fallbackMethod = "fallbackGetAll")
    public List<InvoiceResponse> getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size
    ) {
        return repo.findAll()
                .page(page, size)
                .list()
                .stream()
                .map(inv -> {
                    inv.lines.size(); // force lazy loading
                    return InvoiceResponse.from(inv);
                })
                .toList();
    }

    /* ===== CREATE ===== */

    @POST
    @Transactional
    public InvoiceResponse create(@Valid CreateInvoiceRequest req) {

        Invoice invoice = new Invoice();
        invoice.customerName = req.customerName;
        invoice.customerEmail = req.customerEmail;
        invoice.currency = req.currency;
        invoice.issueDate = req.issueDate;
        invoice.dueDate = req.dueDate;
        invoice.status = InvoiceStatus.DRAFT;

        long total = 0;

        for (CreateInvoiceRequest.Line l : req.lines) {
            InvoiceLine line = new InvoiceLine();
            line.invoice = invoice;
            line.description = l.description;
            line.quantity = l.quantity;
            line.unitPriceCents = l.unitPriceCents;
            line.lineTotalCents = l.unitPriceCents * (long) l.quantity;

            total += line.lineTotalCents;
            invoice.lines.add(line);
        }

        invoice.totalCents = total;
        repo.persist(invoice);

        return InvoiceResponse.from(invoice);
    }

    /* ===== GET BY ID ===== */

    @GET
    @Path("/{id}")
    public InvoiceResponse get(@PathParam("id") Long id) {
        Invoice invoice = repo.findById(id);
        if (invoice == null) {
            throw new NotFoundException("Invoice not found");
        }
        invoice.lines.size(); // force lazy loading
        return InvoiceResponse.from(invoice);
    }

    /* ===== UPDATE ===== */

    @PUT
    @Path("/{id}")
    @Transactional
    public InvoiceResponse update(
            @PathParam("id") Long id,
            @Valid CreateInvoiceRequest req
    ) {

        Invoice invoice = repo.findById(id);
        if (invoice == null) {
            throw new NotFoundException("Invoice not found");
        }

        invoice.customerName = req.customerName;
        invoice.customerEmail = req.customerEmail;
        invoice.currency = req.currency;
        invoice.issueDate = req.issueDate;
        invoice.dueDate = req.dueDate;

        // reset lines
        invoice.lines.clear();

        long total = 0;

        for (CreateInvoiceRequest.Line l : req.lines) {
            InvoiceLine line = new InvoiceLine();
            line.invoice = invoice;
            line.description = l.description;
            line.quantity = l.quantity;
            line.unitPriceCents = l.unitPriceCents;
            line.lineTotalCents = l.unitPriceCents * (long) l.quantity;

            total += line.lineTotalCents;
            invoice.lines.add(line);
        }

        invoice.totalCents = total;

        return InvoiceResponse.from(invoice);
    }

    /* ===== DELETE ===== */

    @DELETE
    @Path("/{id}")
    @Transactional
    public void delete(@PathParam("id") Long id) {
        Invoice invoice = repo.findById(id);
        if (invoice == null) {
            throw new NotFoundException("Invoice not found");
        }
        repo.delete(invoice);
    }

    List<InvoiceResponse> fallbackGetAll(
            int page,
            int size
    ) {
        return List.of(); // reponse safe
    }

}
