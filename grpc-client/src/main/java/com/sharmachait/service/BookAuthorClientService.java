package com.sharmachait.service;

import com.google.protobuf.Descriptors;
import com.sharmachait.proto.gen.Author;
import com.sharmachait.proto.gen.BookAuthorServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BookAuthorClientService {

    @GrpcClient("grpc-bookauthor-service")
    BookAuthorServiceGrpc.BookAuthorServiceBlockingStub blockingStub;

    public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId){
        Author request = Author.newBuilder().setAuthorId(authorId).build();
        Author response = blockingStub.getAuthor(request);
        return response.getAllFields();
    }
}
