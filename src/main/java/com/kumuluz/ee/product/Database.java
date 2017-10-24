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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database {
    private static List<Product> products = new ArrayList<>();

    public static void initDatabase() {
        Product p = new Product();
        p.setAccountId("1");
        p.setId("1");
        p.setDescription("Spomin na lepše čase");
        p.setTitle("Stara uniforma");
        p.setPrice(121.12);
        p.setPublishDate(new Date());
        p.setExpirationDate(new Date());
        products.add(p);

        p = new Product();
        p.setAccountId("2");
        p.setId("2");
        p.setDescription("Spomin na grše čase");
        p.setTitle("Nova uniforma");
        p.setPrice(2.1);
        p.setPublishDate(new Date());
        p.setExpirationDate(new Date());
        products.add(p);
    }

    public static List<Product> getProducts() {
        return products;
    }

    public static Product getProduct(String productId) {
        for (Product product : products) {
            if (product.getId().equals(productId))
                return product;
        }

        return null;
    }

    public static void addProduct(Product product) {
        products.add(product);
    }

    public static void deleteProduct(String productId) {
        for (Product product : products) {
            if (product.getId().equals(productId)) {
                products.remove(product);
                break;
            }
        }
    }
}
