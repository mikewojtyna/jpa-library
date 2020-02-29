package pl.sda.library;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Collection;
import java.util.UUID;

public class JpaCatalog implements Catalog {

	private EntityManager entityManager;

	public JpaCatalog(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public Author addAuthor(Author author) {
		runInTransaction(() -> entityManager.persist(author));
		return author;
	}

	@Override
	public Book addBook(Book book) {
		runInTransaction(() -> entityManager.persist(book));
		return book;
	}

	@Override
	public void deleteAuthor(UUID authorId) {
		runInTransaction(() -> {
			Author author = entityManager
				.find(Author.class, authorId);
			Collection<Book> books = author.getBooks();
			books.forEach(b -> b.getAuthors()
				.removeIf(a -> a.getId().equals(authorId)));
			entityManager.remove(author);
		});
	}

	@Override
	public void deleteBook(UUID bookId) {
		runInTransaction(() -> {
			Book book = entityManager.find(Book.class, bookId);
			Collection<Author> authors = book.getAuthors();
			authors.forEach(author -> author.getBooks()
				.removeIf(b -> b.getId().equals(bookId)));
			entityManager.remove(book);
		});
	}

	@Override
	public void assignAuthorWithBook(UUID authorId, UUID bookId) {
		runInTransaction(() -> {
			Author author = entityManager
				.find(Author.class, authorId);
			Book book = entityManager.find(Book.class, bookId);
			book.getAuthors().add(author);
			author.getBooks().add(book);
		});
	}

	@Override
	public void unassignAuthorFromBook(UUID authorId, UUID bookId) {
		runInTransaction(() -> {
			Book book = entityManager.find(Book.class, bookId);
			book.getAuthors().forEach(a -> {
				a.getBooks().removeIf(b -> b.getId()
					.equals(bookId));
			});
			book.getAuthors()
				.removeIf(a -> a.getId().equals(authorId));
		});
	}

	@Override
	public Collection<Book> findAllBooks() {
		return entityManager
			.createNamedQuery("Book.findAll", Book.class)
			.getResultList();
	}

	@Override
	public Collection<Author> findAllAuthors() {
		return entityManager
			.createNamedQuery("Author.findAll", Author.class)
			.getResultList();
	}

	private void runInTransaction(Runnable runnable) {
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		runnable.run();
		transaction.commit();
	}
}
