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

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("products")
public class ProductResource {

    @GET
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
    @Path("{productId}/details")
    public Response getProductDetails(@PathParam("productId") String productId) {
        Product product = Database.getProduct(productId);
        Client client = ClientBuilder.newClient();
        String name = client.target("http://localhost:8080/v1/accounts/"+product.getAccountId()+"/name")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
        return product != null
                ? Response.ok().entity("Owner of '" + product.getTitle() + "' is '" + name + "'.").build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("init")
    public Response initProducts() {
        Database.initDatabase();
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
}
