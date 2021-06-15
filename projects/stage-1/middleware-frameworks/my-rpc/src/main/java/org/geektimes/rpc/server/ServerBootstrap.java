/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.rpc.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.geektimes.rpc.codec.InvocationRequestEncoder;
import org.geektimes.rpc.codec.InvocationResponseDecoder;
import org.geektimes.rpc.context.ServiceContext;
import org.geektimes.rpc.service.DefaultServiceInstance;
import org.geektimes.rpc.service.registry.ServiceRegistry;
import org.geektimes.rpc.transport.InvocationRequestHandler;

import java.util.Collections;

/**
 * 调用服务器
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class ServerBootstrap implements AutoCloseable {

    private final String applicationName;

    private final int port;

    private final ServiceContext serviceContext;

    private final ServiceRegistry serviceRegistry;

    private Bootstrap bootstrap;

    private EventLoopGroup group;

    public ServerBootstrap(String applicationName, int port) {
        this.applicationName = applicationName;
        this.port = port;
        this.serviceContext = ServiceContext.DEFAULT;
        this.serviceRegistry = ServiceRegistry.DEFAULT;
    }

    public ServerBootstrap registerService(String serviceName, Object service) {
        serviceContext.registerService(serviceName, service);
        return this;
    }

    public ServerBootstrap start() {

        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("request-encoder", new InvocationRequestEncoder());
                        ch.pipeline().addLast("response-decoder", new InvocationResponseDecoder());
                        ch.pipeline().addLast("request-handler", new InvocationRequestHandler(serviceContext));
                    }
                });

        ChannelFuture channelFuture = bootstrap.bind(port);
        // 注册服务
        registerServer();
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }

    private void registerServer() {
        DefaultServiceInstance serviceInstance = new DefaultServiceInstance();
        serviceInstance.setHost("127.0.0.1");
        serviceInstance.setPort(port);
        serviceInstance.setServiceName(applicationName);
        serviceRegistry.initialize(Collections.emptyMap());
        serviceRegistry.register(serviceInstance);
    }

    @Override
    public void close() throws Exception {
        group.shutdownGracefully();
    }
}
