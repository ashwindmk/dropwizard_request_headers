package com.ashwin.api.resource;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.util.*;
//import com.google.common.collect.LinkedHashMultimap;
//import org.glassfish.jersey.internal.RuntimeDelegateDecorator;
//import org.glassfish.jersey.internal.util.collection.ImmutableMultivaluedMap;
//import org.glassfish.jersey.internal.util.collection.Views;
//import javax.ws.rs.ext.RuntimeDelegate;
//import java.net.URI;
//import java.util.stream.Collector;
//import java.util.stream.Collectors;

@Path("/ping")
public class PingResource {
    @GET
    @Path("/headerparam")
    public Response pingHeaderParam(@HeaderParam("user-agent") String userAgent) {
        System.out.println("Headers | user-agent: " + userAgent);
        return Response.ok().build();
    }

    @GET
    @Path("/context")
    public Response pingContext(@Context HttpHeaders headers) {
        System.out.println("Headers | headers: " + headers.getRequestHeaders());
        return Response.ok().build();
    }

    @GET
    @Path("/forward")
    public Response forward(@Context HttpHeaders headers) {
        System.out.println("forward | headers | headers.user-agent: " + headers.getHeaderString(HttpHeaders.USER_AGENT));
        System.out.println("forward | headers | headers: " + headers.getRequestHeaders());

        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, Object> map = asObjectHeaders(headers.getRequestHeaders());

        String url = "http://localhost:9090/redirect";
        WebTarget webTarget = client.target(url);
        Invocation invocation = webTarget.request().headers(map).build("GET");
        return invocation.invoke();
    }

    @GET
    @Path("/location")
    public Response location(@Context HttpHeaders headers) {
        System.out.println("location | Headers | headers.user-agent: " + headers.getHeaderString(HttpHeaders.USER_AGENT));
        System.out.println("location | Headers | headers: " + headers.getRequestHeaders());

        Client client = ClientBuilder.newClient();
        MultivaluedMap<String, Object> map = asObjectHeaders(headers.getRequestHeaders());

        String url = "http://localhost:9090/redirect";
        WebTarget webTarget = client.target(url);
        Invocation invocation = webTarget.request().headers(map).build("GET");
        Response response = invocation.invoke();

        String location = null;
        if (response.getStatus() == Response.Status.FOUND.getStatusCode()
                || response.getStatus() == Response.Status.TEMPORARY_REDIRECT.getStatusCode()) {
            MultivaluedMap<String, Object> responseHeaders = response.getHeaders();
            location = String.valueOf(responseHeaders.getFirst(HttpHeaders.LOCATION));
            System.out.println("location | responseHeaders : " + responseHeaders);
        }

        return Response.seeOther(UriBuilder.fromUri(location).build()).status(Response.Status.MOVED_PERMANENTLY).build();
        //return Response.ok(location).build();
    }

//    public static MultivaluedMap<String, Object> asObjectHeaders(MultivaluedMap<String, String> headers) {
//        if (headers == null) {
//            return null;
//        } else {
//            return new AbstractMultivaluedMap<String, Object>(Views.mapView(headers, LinkedList::new)) {};
//        }
//    }

//    public static MultivaluedMap<String, Object> asObjectHeaders(MultivaluedMap<String, String> headers) {
//        if (headers == null) {
//            return null;
//        } else {
//            MultivaluedMap<String, Object> map = new MultivaluedHashMap<>();
//            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
//                if (entry.getValue() != null) {
//                    map.addAll(entry.getKey(), new ArrayList<Object>(entry.getValue()));
//                }
//            }
//            return map;
//        }
//    }

    public static MultivaluedMap<String, Object> asObjectHeaders(MultivaluedMap<String, String> headers) {
        if (headers == null) {
            return null;
        } else {
            return new AbstractMultivaluedMap<String, Object>(Maps.transformValues(headers, new Function<List<String>, List<Object>>() {
                @Override
                public @Nullable List<Object> apply(@Nullable List<String> strings) {
                    if (strings != null) {
                        return new LinkedList<>(strings);
                    } else {
                        return null;
                    }
                }
            })) {};
        }
    }
}
