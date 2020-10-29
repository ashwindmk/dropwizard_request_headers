package com.ashwin;

import com.ashwin.api.resource.PingResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class RequestHeadersApplication extends Application<RequestHeadersConfiguration> {
    public static void main(String[] args) throws Exception {
        new RequestHeadersApplication().run(args);
    }

    @Override
    public void run(RequestHeadersConfiguration config, Environment env) throws Exception {
        env.jersey().register(new PingResource());
    }
}
