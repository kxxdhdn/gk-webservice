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
package com.example.customerservice.client;

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Starter that initializes the spring context, fetches our test client and then shuts down spring
 */
public final class CustomerServiceSpringClient {
    private CustomerServiceSpringClient() {
    }

    public static void main(String[] args) throws Exception {
        try (ClassPathXmlApplicationContext context
            = new ClassPathXmlApplicationContext(new String[] {"classpath:client-applicationContext.xml"})) {
            CustomerServiceTester client = (CustomerServiceTester)context.getBean("tester");
            client.testCustomerService();
        }
        
        // Shutdown ActiveMQ client pools
        ActiveMQClient.clearThreadPools();
    }
}
