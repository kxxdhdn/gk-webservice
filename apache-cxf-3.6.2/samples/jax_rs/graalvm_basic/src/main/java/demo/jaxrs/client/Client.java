/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package demo.jaxrs.client;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

public final class Client {

    private Client() {
    }

    public static void main(String[] args) throws Exception {
        // Sent HTTP GET request to query customer info
        System.out.println("Sent HTTP GET request to query customer info");
        javax.ws.rs.client.Client client = ClientBuilder.newClient();
        
        String response = client
            .target("http://localhost:9000/customerservice/customers/123")
            .request()
            .get(String.class);
        System.out.println(response);

        // Sent HTTP GET request to query sub resource product info
        System.out.println("\n");
        System.out.println("Sent HTTP GET request to query sub resource product info");

        response = client
            .target("http://localhost:9000/customerservice/orders/223/products/323")
            .request()
            .get(String.class);
        System.out.println(response);

        // Sent HTTP PUT request to update customer info
        System.out.println("\n");
        System.out.println("Sent HTTP PUT request to update customer info");
        
        try (Response r = client
                .target("http://localhost:9000/customerservice/customers")
                .request()
                .put(Entity.xml(client.getClass().getResourceAsStream("/update_customer.xml")))) {

            System.out.println("Response status code: " + r.getStatus());
            System.out.println("Response body: ");
            System.out.println(r.readEntity(String.class));
        }
        
        // Sent HTTP POST request to add customer
        System.out.println("\n");
        System.out.println("Sent HTTP POST request to add customer");

        try (Response r = client
                .target("http://localhost:9000/customerservice/customers")
                .request()
                .post(Entity.xml(client.getClass().getResourceAsStream("/add_customer.xml")))) {

            System.out.println("Response status code: " + r.getStatus());
            System.out.println("Response body: ");
            System.out.println(r.readEntity(String.class));
        }

        System.out.println("\n");
        System.exit(0);
    }
}
