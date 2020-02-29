package pl.sda.library;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a")
@Entity
@Data
public class Author {

	@Id
	@GeneratedValue
	private UUID id;
	@ManyToMany
	// @formatter:off
	@JoinTable(
		name = "Author_Book",
		joinColumns = @JoinColumn(name = "author_id"),
		inverseJoinColumns = @JoinColumn(name = "book_id")
		)
	// @formatter:on
	private Collection<Book> books = new ArrayList<>();

	@Override
	public String toString() {
		return "Author{" + "id=" + id + '}';
	}
}
