package com.sharmachait.service;

import com.google.protobuf.Descriptors;
import com.sharmachait.SeedDB;
import com.sharmachait.proto.gen.Author;
import com.sharmachait.proto.gen.Book;
import com.sharmachait.proto.gen.BookAuthorServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class BookAuthorClientService {

    @GrpcClient("grpc-bookauthor-service")
    BookAuthorServiceGrpc.BookAuthorServiceBlockingStub blockingStub;

    @GrpcClient("grpc-bookauthor-service")
    BookAuthorServiceGrpc.BookAuthorServiceStub asyncClient;

    public Map<Descriptors.FieldDescriptor, Object> getAuthor(int authorId){
        Author request = Author.newBuilder().setAuthorId(authorId).build();
        Author response = blockingStub.getAuthor(request);
        return response.getAllFields();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getBooksByAuthor(int authorId) {
        Author request = Author.newBuilder().setAuthorId(authorId).build();
        final List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        CompletableFuture<Void> done = streamResponse(request, response);

        try {
            done.get(); // Wait for stream completion
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Preserve interrupt status
            log.error("Interrupted while waiting for streaming response", e);
        } catch (ExecutionException e) {
            log.error("gRPC streaming failed: {}", e.getCause().getMessage());
        }
        log.info(response.toString());
        return response;
    }

    private CompletableFuture<Void> streamResponse(Author request, List<Map<Descriptors.FieldDescriptor, Object>> response) {
        CompletableFuture<Void> done = new CompletableFuture<>();

        asyncClient.getBooksByAuthor(request, new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response.add(book.getAllFields());
            }
            @Override
            public void onError(Throwable throwable) {
                done.completeExceptionally(throwable);
            }
            @Override
            public void onCompleted() {
                done.complete(null);
            }
        });

        return done;
    }

    public Map<Descriptors.FieldDescriptor, Object> getExpensiveBook(){
        Map<Descriptors.FieldDescriptor, Object>[] response = new Map[]{null};
        CompletableFuture<Void> future = new CompletableFuture<>();
        StreamObserver<Book> responseObserver = asyncClient.getExpensiveBook(getExpensiveBookObserver(response, future));
        SeedDB.getBooksFromTempDb().forEach(responseObserver::onNext);
        responseObserver.onCompleted();
        future.join();
        return response[0];
    }

    private StreamObserver<Book> getExpensiveBookObserver(
            Map<Descriptors.FieldDescriptor, Object>[] response,
            CompletableFuture<Void> future
    ) {
        return new StreamObserver<Book>() {
            @Override
            public void onNext(Book book) {
                response[0] = book.getAllFields();
            }
            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                throwable.printStackTrace();
                future.completeExceptionally(throwable);
            }
            @Override
            public void onCompleted() {
                future.complete(null);
            }
        };
    }
}
