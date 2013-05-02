package controllers;

import models.EmailToUserDbo;
import models.UserDbo;

import com.alvazan.play.NoSql;

import controllers.auth.Secure;
import controllers.auth.Secure.Security;
import play.mvc.Controller;

public class Register extends Controller {

	public static void postRegister(String email, String password,
			String verifyPassword) throws Throwable {
		validation.required(email);
		/*
		 * if(checkbox==false){ validation.addError("checkbox",
		 * "Please agree to terms and conditions"); }
		 */
		if (password == null) {
			validation.addError("password", "Password must be supplied");
		}
		if (!password.equals(verifyPassword)) {
			validation.addError("verifyPassword", "Passwords did not match");
		}
		if (!email.contains("@"))
			validation.addError("email", "This is not a valid email");

		EmailToUserDbo existing = NoSql.em().find(EmailToUserDbo.class, email);
		if (existing != null) {
			validation.addError("email", "This email already exists");
		}

		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			Application.signup();
		}

		UserDbo user = new UserDbo();
		user.setEmail(email);
		user.setPassword(password);
		user.setManager(user);
		NoSql.em().put(user);

		EmailToUserDbo emailToUser = new EmailToUserDbo();
		emailToUser.setId(email);
		emailToUser.setValue(user.getId());
		NoSql.em().put(emailToUser);

		NoSql.em().flush();

		Secure.addUserToSession(user.getEmail());

		OtherStuff.company();
	}

	public static void userRegister(String email) {
		render(email);
	}

	public static void postUserRegister(String email) {
		render(email);
	}
}
