package judgels.jophiel;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/api/version")
public class VersionResource {
    @Inject
    public VersionResource() {}

    @GET
    @Produces(TEXT_PLAIN)
    public String getVersion() {
        return VersionResource.class.getPackage().getImplementationVersion();
    }
}
