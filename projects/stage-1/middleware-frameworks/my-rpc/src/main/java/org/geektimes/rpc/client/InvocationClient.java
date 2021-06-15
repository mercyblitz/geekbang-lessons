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
package org.geektimes.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.geektimes.rpc.codec.InvocationRequestEncoder;
import org.geektimes.rpc.codec.InvocationResponseDecoder;
import org.geektimes.rpc.service.ServiceInstance;
import org.geektimes.rpc.transport.InvocationResponseHandler;

/**
 * 调用客户端
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class InvocationClient implements AutoCloseable {

    private final String host;

    private final int port;

    private Bootstrap bootstrap;

    private EventLoopGroup group;

    public InvocationClient(ServiceInstance serviceInstance) {
        this.host = serviceInstance.getHost();
        this.port = serviceInstance.getPort();
    }

    public void start() {
        this.bootstrap = new Bootstrap();
        this.group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("request-encoder", new InvocationRequestEncoder());
                        ch.pipeline().addLast("response-decoder", new InvocationResponseDecoder());
                        ch.pipeline().addLast("response-handler", new InvocationResponseHandler());
                    }
                });
    }

    public ChannelFuture connect() {
        return bootstrap.connect(host, port);
    }

    @Override
    public void close() throws Exception {
        group.shutdownGracefully();
    }
}
