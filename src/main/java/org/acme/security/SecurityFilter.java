package org.acme.security;

import io.vertx.ext.web.RoutingContext;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext ctx) {

        String path = ctx.getUriInfo().getPath();

        // On protège uniquement /ui/*
        if (!path.startsWith("ui")) {
            return;
        }

        // Récupération du RoutingContext (Quarkus-native)
        RoutingContext rc = (RoutingContext)
                ctx.getProperty(RoutingContext.class.getName());

        // Pas de session ou pas d'utilisateur => redirect login
        if (rc == null ||
                rc.session() == null ||
                rc.session().get("user") == null) {

            ctx.abortWith(
                    Response.seeOther(
                            URI.create("/login")
                    ).build()
            );
        }
    }
}
