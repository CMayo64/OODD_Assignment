package org.solent.com528.project.impl.rest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;


@ApplicationPath("/rest")
public class RestApp extends ResourceConfig {
    public RestApp() {
        packages("org.solent.com528.project.impl.rest");
    }
}
