package controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.ProfilePic;
import models.User;
import models.UserProfile;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.Blob;
import play.mvc.Before;
import play.mvc.Controller;
import play.server.Server;

public class UserProfileC extends Controller {
	
	@Before
	public static void setConnectedUser() {
		if(Security.isConnected()) {
			User user = User.findByEmail(Security.connected());
			renderArgs.put("user", user.name);
		}
	}
	
	public static void list() {
		List<User> allUsers = User.findAll();
		render(allUsers);
	}
	
	public static void show(long userId) {		
		List<String> tabIds = new ArrayList<String>();
		tabIds.add("merit");
		tabIds.add("questions-asked");
		tabIds.add("answers-provided");
		tabIds.add("challenges-taken");
		List<String> tabNames = new ArrayList<String>();
		tabNames.add("Merit");
		tabNames.add("Questions Asked");
		tabNames.add("Answers Provided");
		tabNames.add("Challenges Taken");
		
		UserProfile userProfile = getUserProfileFromUserId(userId);
		
		render(userProfile, tabIds, tabNames);
	}
	
	public static void change(String username, 
							  String oldPassword, 
							  @Required @MinSize(5) String newPassword, 
							  @Required @MinSize(5) String newPassword2) {
		if (validation.hasErrors()) {
            validation.keep();
            params.flash();
            flash.error("Please correct these errors !");
            User userByUsername = User.findByEmail(username);
			show(userByUsername.id);
        }
		User user = 
			User.find("byEmailAndPasswordHash", username, oldPassword).first();
		if(user != null) {
			if(newPassword.equals(newPassword2)) {
				user.passwordHash = newPassword;
				user.save();
			} else {
				flash.error("Sorry, the fields 'New Password' and 'Retype New Password' did not match.");				
			}
			show(user.id);
		} else {
			flash.error("Sorry, the oldPassword did not match, please  again.");
			User userByUsername = User.findByEmail(username);
			show(userByUsername.id);
		}
	}
	
	public static void pic(long userId) {
		UserProfile userProfile = UserProfile.find("select distinct upr from UserProfile upr where upr.user.id = ?", userId).first();
		renderBinary(userProfile.profilePic.image.get());
	}
	
	public static void update(long userId, 
							  String aboutMyself, 
							  String location, 
							  Blob profilePicBlob) {
		
		UserProfile userProfile = UserProfile.find("select distinct upr from UserProfile upr where upr.user.id = 1").first();
		if(profilePicBlob != null) {
			//TODO: Delete the old profile pic
			userProfile.profilePic = new ProfilePic(profilePicBlob).save();
		}
		userProfile.aboutMyself = aboutMyself;
		userProfile.location = location;
		userProfile.save();
		
		show(userId);
	}
	
	private static UserProfile getUserProfileFromUserId(long userId) {
		String sql = "select distinct upr from UserProfile upr where upr.user.id = ?";
		return UserProfile.find(sql, userId).first();
	}
}
