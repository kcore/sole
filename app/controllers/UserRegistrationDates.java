package controllers;

import play.mvc.With;

@Check("admin")
@With(Security.class)
public class UserRegistrationDates extends CRUD {

}
