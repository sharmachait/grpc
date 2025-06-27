package com.sharmachait;

import com.sharmachait.proto.gen.Author;
import com.sharmachait.proto.gen.BookAuthorServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class BookAuthorServerService extends BookAuthorServiceGrpc.BookAuthorServiceImplBase {
    @Override
    public void getAuthor(Author request, StreamObserver<Author> responseObserver) {
        SeedDB.getAuthorsFromTempDb().stream()
                .filter(author -> author.getAuthorId() == request.getAuthorId())
                .findFirst()
                .ifPresentOrElse(responseObserver::onNext,
                        ()->{responseObserver.onError(new RuntimeException("Author not found"));}
                );

        responseObserver.onCompleted();
    }
}
