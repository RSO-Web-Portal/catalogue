/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.product;


import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.discovery.enums.AccessType;
import com.kumuluz.ee.discovery.utils.DiscoveryUtil;
import com.kumuluz.ee.health.HealthRegistry;
import com.kumuluz.ee.product.health.ProductDiscoveryHealthCheckBean;
import com.kumuluz.ee.product.mejnik.Mejnik1;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("products")
@RequestScoped
public class ProductResource {


    @Inject
    @DiscoverService(value = "account-service", version = "1.0.x", environment = "dev")
    private Optional<WebTarget> target;

    @Inject
    private DiscoveryUtil discoveryUtil;


    @GET
    @Metered(name = "get-all-products-requests")
    public Response getAllProducts() {
        List<Product> products = Database.getProducts();
        return Response.ok(products).build();
    }

    @GET
    @Path("{productId}")
    public Response getProduct(@PathParam("productId") String productId) {
        Product product = Database.getProduct(productId);
        return product != null
                ? Response.ok(product).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("{productId}/owner")
    public Response getProductDetails(@PathParam("productId") String productId) {
        Product product = Database.getProduct(productId);
        //Client client = ClientBuilder.newClient();
        /*String name = client.target("http://localhost:8080/v1/accounts/"+product.getAccountId()+"/name")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);*/

        String name = "";
        if (!target.isPresent()) {
            System.out.println("NI NAJDEN!!!");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        WebTarget accountService = target.get().path("v1/accounts/"+product.getAccountId()+"/name");

        Response response;
        try {
            response = accountService.request().get();
        } catch (ProcessingException e) {
            return Response.status(408).build();
        }

        return product != null
                ? Response.ok().entity(response.getEntity()).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("init")
    public Response initProducts() {
        Database.initDatabase();

        // DIscovery health check....injecta se null
        //HealthRegistry.getInstance().register(ProductDiscoveryHealthCheckBean.class.getSimpleName(), new ProductDiscoveryHealthCheckBean());

        return Response.ok().build();
    }

    @POST
    public Response addNewProduct(Product product) {
        Database.addProduct(product);
        return Response.noContent().build();
    }

    @DELETE
    @Path("{productId}")
    public Response deleteProduct(@PathParam("productId") String productId) {
        Database.deleteProduct(productId);
        return Response.noContent().build();
    }

    @GET
    @Path("mejnik1")
    public Response mejnik1() {
        Mejnik1 m = new Mejnik1();
        m.setClani(new String[] {"zs3179", "ns3868"});
        m.setOpis_projekta("Nas projekt implemenitra boljšo bolho - portal za prodajo in nakupovanje različnih izdelkov");
        m.setMikrostoritve(new String[] {"http://placeholder/v1/accounts", "http://placeholder/v1/products"});
        m.setGithub(new String[] {"https://github.com/RSO-Web-Portal/account-service", "https://github.com/RSO-Web-Portal/catalogue-service"});
        m.setTravis(new String[] {"https://travis-ci.org/RSO-Web-Portal/account-service", "https://travis-ci.org/RSO-Web-Portal/catalogue-service"});
        m.setDockerhub(new String[] {"https://hub.docker.com/r/rsodocker123/account-service", "https://hub.docker.com/r/rsodocker123/catalogue-service/"});

        return Response.ok().entity(m).build();
    }
}
