package com.sharmachait.interceptors;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

import java.util.Objects;

@Slf4j
@GrpcGlobalServerInterceptor
public class ApiKeyAuthInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> serverCall,
            Metadata metadata,
            ServerCallHandler<ReqT, RespT> serverCallHandler) {
        log.info("Server interceptor {}", serverCall.getMethodDescriptor());
        Metadata.Key<String> apiKeyMetadata = Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER);
        String apiKey = metadata.get(apiKeyMetadata);
        log.info("API key received {}", apiKey);
        if(Objects.nonNull(apiKey) && apiKey.equals("myapikey")){
            return serverCallHandler.startCall(serverCall, metadata);
        }
        Status status = Status.UNAUTHENTICATED.withDescription("Invalid api-key");
        serverCall.close(status, metadata);
        return new ServerCall.Listener<>(){};
    }
}
