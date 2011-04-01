package controllers;

import java.util.List;

import models.BlogPost;
import models.Comment;
import models.Role;
import models.User;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;

public class BlogPostC extends Controller {
	
	@Before
	public static void setConnectedUser() {
		if(Security.isConnected()) {
			User user = User.findByEmail(Security.connected());
			renderArgs.put("user", user.name);
		}
	}
	
	public static void list() {
		List<BlogPost> blogPosts = BlogPost.findAll();
		render(blogPosts);
	}
	
	public static  void show(long id) {
		BlogPost blogPost = BlogPost.findById(id);
		render(blogPost);
	}
	
	public static void form(BlogPost blogPost) {
		if(Security.isConnected() && Security.check("admin")) {
			render(blogPost);
		}
		else {
			renderText("You are not authorized to view this page");
		}
	}
	
	public static void save(@Required String title, 
							@Required String content, 
							String tags) {		
		
		if(!(Security.isConnected() && Security.check("admin"))) {
			renderText("Sorry you do not have authorization to view this page");
		}
		User author = User.findByEmail(Security.connected());
		BlogPost blogPost = new BlogPost(author, title, content);
		if(validation.hasErrors()) {
			validation.keep();
			params.flash();
			flash.error("Please correct these errors");
			render("@form", blogPost);
		}
//		
//		validation.valid(blogPost);
//		if(validation.hasErrors()) {
//			render("@form", blogPost);
//		}
		
		blogPost.save();
		list();		
	}
	
	public static void postComment(long postId, 
								   String name, 
								   String email, 
								   String website, 
								   String message) {
		
		BlogPost blogPost = BlogPost.findById(postId);
		Comment comment = new Comment(message, name, email, website);
		comment.save();
		blogPost.comments.add(comment);
		blogPost.save();
		show(blogPost.id);
	}
}