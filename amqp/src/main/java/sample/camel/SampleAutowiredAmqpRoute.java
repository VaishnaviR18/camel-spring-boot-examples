/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sample.camel;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SampleAutowiredAmqpRoute extends RouteBuilder {
    @Autowired
    JmsConnectionFactory amqpConnectionFactory;

    @Bean
    public AMQPComponent amqpConnection() {
        AMQPComponent amqp = new AMQPComponent();
        amqp.setConnectionFactory(amqpConnectionFactory);
        return amqp;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet");

        rest().post("/").to("direct:send");
        from("direct:send")
                .setExchangePattern(ExchangePattern.InOnly)
                .to("amqp:queue:example")
                .log("Message sent to AMQP queue");

        from("amqp:queue:example").log("Received message from AMQP queue: ${body}");
    }

}
