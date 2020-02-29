package pl.sda.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


public class CatalogTest {

	private EntityManager entityManager;
	private Catalog catalog;

	@BeforeEach
	void setup() {
		entityManager = Persistence
			.createEntityManagerFactory("library")
			.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		entityManager.createQuery("DELETE FROM Author");
		entityManager.createQuery("DELETE FROM Book");
		transaction.commit();
		catalog = initCatalog();
	}

	// @formatter:off
	@DisplayName(
		"adding a new book"
	)
	// @formatter:on
	@Test
	void add() throws Exception {
		// given
		Book book = anyBook();

		// when
		catalog.addBook(book);

		// then
		Collection<Book> allBooks = catalog.findAllBooks();
		assertThat(allBooks).hasSize(1);
	}

	// @formatter:off
	@DisplayName(
		"add new author"
	)
	// @formatter:on
	@Test
	void addAuthor() throws Exception {
		// given
		Author author = anyAuthor();

		// when
		catalog.addAuthor(author);

		// then
		assertThat(catalog.findAllAuthors()).hasSize(1);
	}

	// @formatter:off
	@DisplayName(
		"given author and book in catalog, " +
		"when add author to book, " +
		"then book is written by the author"
	)
	// @formatter:on
	@Test
	void addAuthorToBook() throws Exception {
		// given
		Author authorInCatalog = catalog.addAuthor(anyAuthor());
		Book bookInCatalog = catalog.addBook(anyBook());

		// when
		catalog.assignAuthorWithBook(authorInCatalog
			.getId(), bookInCatalog.getId());

		// then
		Book foundBook = catalog.findAllBooks().stream().findAny()
			.get();
		assertThat(foundBook.getAuthors())
			.containsExactly(authorInCatalog);
	}

	// @formatter:off
	@DisplayName(
		"given author A and author B " +
		"and book written by author A, " +
		"when add author B to book, " +
		"then book is written also by author B"
	)
	// @formatter:on
	@Test
	void addAnotherAuthorToBook() throws Exception {
		// given
		Author authorA = catalog.addAuthor(anyAuthor());
		Author authorB = catalog.addAuthor(anyAuthor());
		Book bookInCatalog = catalog.addBook(anyBook());
		catalog.assignAuthorWithBook(authorA.getId(), bookInCatalog
			.getId());

		// when
		catalog.assignAuthorWithBook(authorB.getId(), bookInCatalog
			.getId());

		// then
		Book foundBook = catalog.findAllBooks().stream().findAny()
			.get();
		assertThat(foundBook.getAuthors())
			.containsExactlyInAnyOrder(authorA, authorB);
	}

	// @formatter:off
	@DisplayName(
		"given author without books and books A and B, " +
		"when add both books to author, " +
		"then author wrote these two books"
	)
	// @formatter:on
	@Test
	void addBooksToAuthor() throws Exception {
		// given
		Author author = catalog.addAuthor(anyAuthor());
		Book bookA = catalog.addBook(anyBook());
		Book bookB = catalog.addBook(anyBook());

		// when
		catalog.assignAuthorWithBook(author.getId(), bookA.getId());
		catalog.assignAuthorWithBook(author.getId(), bookB.getId());

		// then
		Author foundAuthor =
			catalog.findAllAuthors().stream().findAny()
			.get();
		Collection<Book> booksWrittenByAuthor = foundAuthor.getBooks();
		assertThat(booksWrittenByAuthor)
			.containsExactlyInAnyOrder(bookA, bookB);
		assertThat(booksWrittenByAuthor.stream()
			.flatMap(book -> book.getAuthors().stream()))
			.containsOnly(author);
	}

	// @formatter:off
	@DisplayName(
		"given author who wrote a book, " +
		"when delete author, " +
		"then author is deleted and book is left, but has no author"
	)
	// @formatter:on
	@Test
	void removeAuthor() throws Exception {
		// given
		Author author = catalog.addAuthor(anyAuthor());
		Book book = catalog.addBook(anyBook());
		catalog.assignAuthorWithBook(author.getId(), book.getId());

		// when
		catalog.deleteAuthor(author.getId());

		// then
		assertThat(catalog.findAllAuthors()).isEmpty();
		Book foundBook = catalog.findAllBooks().stream().findAny()
			.get();
		assertThat(foundBook.getAuthors()).isEmpty();
	}

	// @formatter:off
	@DisplayName(
		"given author who wrote a book, " +
		"when delete book, " +
		"then book is deleted and author is left, but has no books"
	)
	// @formatter:on
	@Test
	void removeBook() throws Exception {
		// given
		Author author = catalog.addAuthor(anyAuthor());
		Book book = catalog.addBook(anyBook());
		catalog.assignAuthorWithBook(author.getId(), book.getId());

		// when
		catalog.deleteBook(book.getId());

		// then
		assertThat(catalog.findAllBooks()).isEmpty();
		Author foundAuthor =
			catalog.findAllAuthors().stream().findAny()
			.get();
		assertThat(foundAuthor.getBooks()).isEmpty();
	}

	// @formatter:off
	@DisplayName(
		"given author who wrote a book, " +
		"when unassign author from this book, " +
		"then book has no authors and author has no books"
	)
	// @formatter:on
	@Test
	void unassignAuthor() throws Exception {
		// given
		Author author = catalog.addAuthor(anyAuthor());
		Book book = catalog.addBook(anyBook());
		catalog.assignAuthorWithBook(author.getId(), book.getId());

		// when
		catalog.unassignAuthorFromBook(author.getId(), book.getId());

		// then
		Author foundAuthor =
			catalog.findAllAuthors().stream().findAny()
			.get();
		assertThat(foundAuthor.getBooks()).isEmpty();
		Book foundBook = catalog.findAllBooks().stream().findAny()
			.get();
		assertThat(foundBook.getAuthors()).isEmpty();
	}

	private Author anyAuthor() {
		return new Author();
	}

	private Catalog initCatalog() {
		return new JpaCatalog(entityManager);
	}

	private Book anyBook() {
		return new Book();
	}
}
