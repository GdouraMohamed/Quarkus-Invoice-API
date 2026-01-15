package org.acme.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.domain.Invoice;
import org.acme.domain.InvoiceLine;
import org.acme.domain.InvoiceStatus;
import org.acme.repo.InvoiceRepository;

import java.time.LocalDate;

@Path("/ui/invoices")
@Produces(MediaType.TEXT_HTML)
public class InvoiceUiResource {

    private final InvoiceRepository repo;
    private final Template invoices;
    private final Template invoice;
    @Inject
    Template invoiceCreate;

    public InvoiceUiResource(
            InvoiceRepository repo,
            Template invoices,
            Template invoice
    ) {
        this.repo = repo;
        this.invoices = invoices;
        this.invoice = invoice;
    }

    @GET
    public TemplateInstance list() {
        return invoices.data("invoices", repo.listAll());
    }

    @GET
    @Path("/{id}")
    public TemplateInstance detail(@PathParam("id") Long id) {
        var inv = repo.findById(id);
        if (inv != null) {
            inv.lines.size(); // force lazy load
        }
        return invoice.data("invoice", inv);
    }

    /* ===== CREATE FORM ===== */

    @GET
    @Path("/create")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance createForm() {
        return invoiceCreate.instance();
    }

    /* ===== HANDLE SUBMIT ===== */

    @POST
    @Path("/create")
    @Transactional
    public Response createInvoice(
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("currency") String currency,
            @FormParam("issueDate") LocalDate issueDate,
            @FormParam("dueDate") LocalDate dueDate,
            @FormParam("lineDescription") String lineDescription,
            @FormParam("quantity") int quantity,
            @FormParam("unitPriceCents") long unitPriceCents
    ) {

        Invoice invoice = new Invoice();
        invoice.customerName = customerName;
        invoice.customerEmail = customerEmail;
        invoice.currency = currency;
        invoice.issueDate = issueDate;
        invoice.dueDate = dueDate;
        invoice.status = InvoiceStatus.DRAFT;

        InvoiceLine line = new InvoiceLine();
        line.invoice = invoice;
        line.description = lineDescription;
        line.quantity = quantity;
        line.unitPriceCents = unitPriceCents;
        line.lineTotalCents = quantity * unitPriceCents;

        invoice.lines.add(line);
        invoice.totalCents = line.lineTotalCents;

        repo.persist(invoice);

        return Response.seeOther(
                java.net.URI.create("/ui/invoices")
        ).build();
    }
}
