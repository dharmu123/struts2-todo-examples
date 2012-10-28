package eu.strutters.example.todo.model;

import javax.persistence.*;
import java.util.Date;

/**
 * TodoItem.
 *
 * @author Rene Gielen
 */
@Entity
public class TodoItem {

	private Integer id;
	private Date dueDate;
	private String topic;
	private String description;
	private String category;
	private boolean done;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTopic() {
		return topic;
	}

	@Temporal(TemporalType.DATE)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Column(length = 1024)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}
}
