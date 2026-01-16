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

import java.net.URI;
import java.time.LocalDate;

@Path("/ui/invoices")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
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

    /* ===== LIST ===== */

    @GET
    public TemplateInstance list() {
        return invoices.data("invoices", repo.listAll());
    }

    /* ===== DETAIL ===== */

    @GET
    @Path("/{id}")
    public TemplateInstance detail(@PathParam("id") Long id) {
        Invoice inv = repo.findById(id);
        if (inv == null) {
            throw new NotFoundException();
        }
        inv.lines.size();
        return invoice.data("invoice", inv);
    }

    /* ===== CREATE FORM ===== */

    @GET
    @Path("/create")
    public TemplateInstance createForm() {
        return invoiceCreate.instance();
    }

    /* ===== CREATE ===== */
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response create(
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("currency") String currency,
            @FormParam("issueDate") LocalDate issueDate,
            @FormParam("dueDate") LocalDate dueDate,
            @FormParam("lineDescription") String lineDescription,
            @FormParam("quantity") int quantity,
            @FormParam("unitPriceCents") long unitPriceCents
    ) {

        Invoice inv = new Invoice();
        inv.customerName = customerName;
        inv.customerEmail = customerEmail;
        inv.currency = currency;
        inv.issueDate = issueDate;
        inv.dueDate = dueDate;
        inv.status = InvoiceStatus.DRAFT;

        InvoiceLine line = new InvoiceLine();
        line.invoice = inv;
        line.description = lineDescription;
        line.quantity = quantity;
        line.unitPriceCents = unitPriceCents;
        line.lineTotalCents = quantity * unitPriceCents;

        inv.lines.add(line);
        inv.totalCents = line.lineTotalCents;

        repo.persist(inv);

        return Response.seeOther(
                java.net.URI.create("/ui/invoices")
        ).build();
    }

    /* ===== EDIT FORM ===== */

    @GET
    @Path("/{id}/edit")
    public TemplateInstance editForm(@PathParam("id") Long id) {
        Invoice inv = repo.findById(id);
        if (inv == null) {
            throw new NotFoundException();
        }
        inv.lines.size();
        return invoiceCreate.data("invoice", inv);
    }

    /* ===== UPDATE ===== */

    @POST
    @Path("/{id}/edit")
    @Transactional
    public Response update(
            @PathParam("id") Long id,
            @FormParam("customerName") String customerName,
            @FormParam("customerEmail") String customerEmail,
            @FormParam("currency") String currency,
            @FormParam("issueDate") LocalDate issueDate,
            @FormParam("dueDate") LocalDate dueDate
    ) {

        Invoice inv = repo.findById(id);
        if (inv == null) {
            throw new NotFoundException();
        }

        inv.customerName = customerName;
        inv.customerEmail = customerEmail;
        inv.currency = currency;
        inv.issueDate = issueDate;
        inv.dueDate = dueDate;

        return Response.seeOther(
                URI.create("/ui/invoices/" + id)
        ).build();
    }

    /* ===== DELETE ===== */

    @POST
    @Path("/{id}/delete")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Invoice inv = repo.findById(id);
        if (inv != null) {
            repo.delete(inv);
        }
        return Response.seeOther(URI.create("/ui/invoices")).build();
    }
}
