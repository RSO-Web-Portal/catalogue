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
import com.kumuluz.ee.discovery.utils.DiscoveryUtil;
import com.kumuluz.ee.fault.tolerance.annotations.CommandKey;
import com.kumuluz.ee.fault.tolerance.annotations.GroupKey;
import com.kumuluz.ee.health.HealthRegistry;
import com.kumuluz.ee.product.health.ProductDiscoveryHealthCheckBean;
import com.kumuluz.ee.product.mejnik.Mejnik1;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.metrics.annotation.Metered;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Array;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.enterprise.context.RequestScoped;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("products")
@GroupKey("ProductResource")
@RequestScoped
public class ProductResource {


    @Inject
    @DiscoverService(value = "order-service", version = "1.0.x", environment = "dev")
    private Optional<WebTarget> target;

    @Inject
    @DiscoverService(value = "priority-service", version = "1.0.x", environment = "dev")
    private Optional<WebTarget> priorityTarget;


    @GET
    @Metered(name = "get-all-products-requests")
    public Response getAllProducts() {
        List<Product> products = Database.getProducts();
        return Response.ok(products).build();
    }

    @GET
    @Path("active")
    public Response getAllActiveProducts() {
        List<Product> products = Database.getProducts();
        List<Product> active = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).isActive()) {
                active.add(products.get(i));
            }
        }

        String[][] toSend = new String[active.size()][2];
        for (int i = 0; i < active.size(); i++) {
            toSend[i][0] = active.get(i).getId();
            toSend[i][1] = active.get(i).getPriorityId();
        }

        WebTarget priorityService = priorityTarget.get().path("v1/priorities/sort");

        Response response = priorityService.request(MediaType.APPLICATION_JSON).post(Entity.json(toSend));

        try {

            String[] sortedIds = response.readEntity(String[].class);

            List<Product> sorted = new ArrayList<>();
            for (String s : sortedIds) {
                for (Product p : active) {
                    if (p.getId().equals(s)) {
                        sorted.add(p);
                        break;
                    }
                }
            }

            return Response.ok(sorted).build();

        } catch (ProcessingException e) {
            return Response.status(408).build();
        }

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
    @Path("{productId}/order")
    @CommandKey("getProductDetails")
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getProductDetailsFallback")
    public Response getProductDetails(@PathParam("productId") String productId) {
        Product product = Database.getProduct(productId);

        String name = "";
        if (!target.isPresent()) {
            System.out.println("NI NAJDEN!!!");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        WebTarget orderService = target.get().path("v1/orders/"+product.getOrderId());

        Response response;
        try {
            response = orderService.request().get();
        } catch (ProcessingException e) {
            return Response.status(408).build();
        }

        return product != null
                ? Response.ok().entity(response.readEntity(Order.class)).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    public Response getProductDetailsFallback(String productId) {
        return Response.accepted("Fallback: /order - order service may not be available or an error has occured.").build();
    }




    @GET
    @Path("{productId}/items")
    public Response getProductDetailsItems(@PathParam("productId") String productId) {
        Product product = Database.getProduct(productId);

        String name = "";
        if (!target.isPresent()) {
            System.out.println("NI NAJDEN!!!");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        WebTarget orderService = target.get().path("v1/orders/"+product.getOrderId() + "/items");

        Response response;
        try {
            response = orderService.request().get();
        } catch (ProcessingException e) {
            return Response.status(408).build();
        }

        return product != null
                ? Response.ok().entity(response.readEntity(Item[].class)).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }


    @POST
    public Response addNewProduct(Product product) {
        System.out.println("DOBIL SEM NEKI!!!!");
        Database.addProduct(product);
        return Response.noContent().build();
    }

    @GET
    @Path("{id}/makeTransaction")
    public Response makeTransaction(@PathParam("id") String id) {
        Product p = Database.getProduct(id);
        if (p == null || !p.isActive()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } else {
            p.setActive(false);
            return Response.status(Response.Status.OK).build();
        }

    }

    @GET
    @Path("init")
    public Response initHealth() {
        // DIscovery health check....injecta se null
        //HealthRegistry.getInstance().register(ProductDiscoveryHealthCheckBean.class.getSimpleName(), new ProductDiscoveryHealthCheckBean());
        return Response.ok().build();
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
        m.setMikrostoritve(new String[] {"http://169.51.17.38:32314/v1/accounts", "http://169.51.17.38:32456/v1/products"});
        m.setGithub(new String[] {"https://github.com/RSO-Web-Portal/account-service", "https://github.com/RSO-Web-Portal/catalogue-service"});
        m.setTravis(new String[] {"https://travis-ci.org/RSO-Web-Portal/account-service", "https://travis-ci.org/RSO-Web-Portal/catalogue-service"});
        m.setDockerhub(new String[] {"https://hub.docker.com/r/rsodocker123/account-service", "https://hub.docker.com/r/rsodocker123/catalogue-service/"});

        return Response.ok().entity(m).build();
    }
}
