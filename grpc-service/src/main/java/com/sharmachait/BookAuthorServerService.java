package com.sharmachait;

import com.sharmachait.proto.gen.Author;
import com.sharmachait.proto.gen.Book;
import com.sharmachait.proto.gen.BookAuthorServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.concurrent.CompletableFuture;

@Slf4j
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

    @Override
    public void getBooksByAuthor(Author request, StreamObserver<Book> responseObserver) {
        SeedDB.getBooksFromTempDb()
                .stream()
                .filter(book -> book.getAuthorId() == request.getAuthorId())
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Book> getExpensiveBook(StreamObserver<Book> responseObserver) {

        return new StreamObserver<Book>() {
            Book mostExpensiveBook = null;
            @Override
            public void onNext(Book book) {
                if(mostExpensiveBook == null)
                    mostExpensiveBook = book;
                else if(book.getPrice() >= mostExpensiveBook.getPrice())
                    mostExpensiveBook = book;
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                throwable.printStackTrace();
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(mostExpensiveBook);
                responseObserver.onCompleted();
            }
        };
    }
}
