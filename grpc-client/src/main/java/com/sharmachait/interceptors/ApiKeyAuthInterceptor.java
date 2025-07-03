package com.sharmachait.interceptors;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;

@Slf4j
@GrpcGlobalClientInterceptor
public class ApiKeyAuthInterceptor implements ClientInterceptor {
    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> methodDescriptor,
            CallOptions callOptions,
            Channel channel) {
        log.info("Client interceptor {}", methodDescriptor.getFullMethodName());
        return new ForwardingClientCall
                .SimpleForwardingClientCall<>(channel.newCall(methodDescriptor, callOptions)){
            @Override
            public void start(Listener<RespT> respTListener, Metadata headers){
                headers.put(Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER), "myapikey");
                super.start(respTListener, headers);
            }
        };
    }
}
