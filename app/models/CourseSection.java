package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class CourseSection extends Model {
	
	@Required
	public String title;
	public int placement;
	
	@Lob
	@MaxSize(20000)
	public String content;

	@Required
	@ManyToOne
	public Course course;
	
	@OneToMany(cascade=CascadeType.ALL)
	public Set<Comment> comments;
	
	@OneToMany(cascade=CascadeType.ALL)
	public Set<Question> questions;
	
	@ManyToMany(cascade=CascadeType.ALL)
	public Set<SocialUser> understoodParticipants;
	
	public CourseSection(Course course, String title, String content) {
		this.course = course;
		this.title = title;
		this.content = content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((course == null) ? 0 : course.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CourseSection other = (CourseSection) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (course == null) {
			if (other.course != null)
				return false;
		} else if (!course.equals(other.course))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.title;
	}	
}

