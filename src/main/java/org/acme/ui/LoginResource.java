package org.acme.ui;

import io.quarkus.qute.*;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.acme.domain.User;
import org.acme.security.AuthService;

import java.net.URI;

@Path("/login")
public class LoginResource {

    private final Template login;
    private final AuthService auth;

    public LoginResource(Template login, AuthService auth) {
        this.login = login;
        this.auth = auth;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance loginForm() {
        return login.instance();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response doLogin(
            @FormParam("username") String username,
            @FormParam("password") String password,
            @Context RoutingContext rc
    ) {
        User user = auth.authenticate(username, password);
        if (user == null) {
            return Response.seeOther(URI.create("/login?error")).build();
        }

        rc.session().put("user", user);

        return Response.seeOther(URI.create("/ui/invoices")).build();
    }
}
