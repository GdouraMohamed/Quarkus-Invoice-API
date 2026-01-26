package org.acme.api;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
//debug user connecté
@Path("/api/me")
@Authenticated
@Produces(MediaType.APPLICATION_JSON)
public class MeResource {

    private final SecurityIdentity identity;

    public MeResource(SecurityIdentity identity) {
        this.identity = identity;
    }

    @GET
    public Object me() {
        return java.util.Map.of(
                "principal", identity.getPrincipal().getName(),
                "roles", identity.getRoles()
        );
    }
}
