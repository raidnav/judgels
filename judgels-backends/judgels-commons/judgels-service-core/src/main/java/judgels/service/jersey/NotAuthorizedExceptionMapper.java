package judgels.service.jersey;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
    @Override
    public Response toResponse(NotAuthorizedException exception) {
        // This is a hack because currently Conjure does not support 401 exception :(

        Map<String, String> serializableError = Maps.newHashMap();
        serializableError.put("errorCode", "CUSTOM_CLIENT");
        serializableError.put("errorName", "Judgels:Unauthorized");
        serializableError.put("errorInstanceId", UUID.randomUUID().toString());

        return Response
                .status(Response.Status.UNAUTHORIZED)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(serializableError)
                .build();
    }
}
