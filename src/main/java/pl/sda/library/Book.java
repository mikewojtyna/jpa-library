package pl.sda.library;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@NamedQuery(name = "Book.findAll", query = "SELECT b FROM Book b")
@Entity
@Data
public class Book {

	@Id
	@GeneratedValue
	private UUID id;
	@ManyToMany(mappedBy = "books")
	private Collection<Author> authors = new ArrayList<>();

	@Override
	public String toString() {
		return "Book{" + "id=" + id + '}';
	}
}
