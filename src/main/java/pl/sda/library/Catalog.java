package pl.sda.library;

import java.util.Collection;
import java.util.UUID;

public interface Catalog {

	Author addAuthor(Author author);

	Book addBook(Book book);

	void deleteAuthor(UUID authorId);

	void deleteBook(UUID bookId);

	void assignAuthorWithBook(UUID authorId, UUID bookId);

	void unassignAuthorFromBook(UUID authorId, UUID bookId);

	Collection<Book> findAllBooks();

	Collection<Author> findAllAuthors();
}
