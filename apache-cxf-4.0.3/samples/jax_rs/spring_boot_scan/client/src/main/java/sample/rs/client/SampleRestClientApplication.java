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
package sample.rs.client;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.LinkedList;
import java.util.List;

import jakarta.ws.rs.core.UriBuilder;

import org.apache.cxf.annotations.Provider;
import org.apache.cxf.annotations.Provider.Type;
import org.apache.cxf.clustering.CircuitBreakerTargetSelector;
import org.apache.cxf.clustering.FailoverStrategy;
import org.apache.cxf.clustering.RandomStrategy;
import org.apache.cxf.clustering.circuitbreaker.CircuitBreakerFailoverFeature;
import org.apache.cxf.jaxrs.client.spring.EnableJaxRsProxyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateDiscoveryClientOptionalArgs;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import sample.rs.service.HelloService;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJaxRsProxyClient
public class SampleRestClientApplication {
    public static void main(String[] args) {
        URL.setURLStreamHandlerFactory(protocol -> "eureka".equals(protocol) ? new URLStreamHandler() {
            protected URLConnection openConnection(URL url) throws IOException {
                return new URLConnection(url) {
                    public void connect() throws IOException {
                    }
                };
            }
        } : null);

        new SpringApplicationBuilder(SampleRestClientApplication.class)
            .web(WebApplicationType.NONE)
            .run(args);
    }
    @Bean
    CommandLineRunner initProxyClient(final HelloService service) {

        return new CommandLineRunner() {
            @Override
            public void run(String... runArgs) throws Exception {
                System.out.println(service.sayHello("ApacheCxfProxyUser1"));
                System.out.println(service.sayHello("ApacheCxfProxyUser2"));
            }
        };
    }

    @Bean
    RestTemplateDiscoveryClientOptionalArgs restTemplateDiscoveryClientOptionalArgs() {
        return new RestTemplateDiscoveryClientOptionalArgs(new DefaultEurekaClientHttpRequestFactorySupplier());
    }

    /**
     * Basic Random selection of statically prepared addresses.
     * More advanced strategies will periodically pull DiscoveryClient
     * to update the addresses list
     */
    @Component
    @Provider(Type.Feature)
    static class EurekaFailoverFeature extends CircuitBreakerFailoverFeature {
        @Autowired
        private DiscoveryClient discoveryClient;
        private List<String> addresses = new LinkedList<>();

        EurekaFailoverFeature() {
            super();
            setDelegate(new EurekaFailoverFeaturePortable(CircuitBreakerTargetSelector.DEFAULT_THESHOLD,
                CircuitBreakerTargetSelector.DEFAULT_TIMEOUT, "eureka://registry"));
        }

        private class EurekaFailoverFeaturePortable extends CircuitBreakerFailoverFeature.Portable {
            EurekaFailoverFeaturePortable(int threshold, long timeout, String clientBootstrapAddress) {
                super(threshold, timeout, clientBootstrapAddress);
            }

            @Override
            public FailoverStrategy getStrategy()  {
                for (ServiceInstance s : discoveryClient.getInstances("jaxrs-hello-world-service")) {
                    UriBuilder ub = UriBuilder.fromUri(s.getUri());
                    if (s.getMetadata().containsKey("servletPath")) {
                        ub.path(s.getMetadata().get("servletPath"));
                    }
                    addresses.add(ub.build().toString());
                }
                RandomStrategy rs = new RandomStrategy();
                rs.setAlternateAddresses(addresses);
                return rs;
            }
        }
    }
}
