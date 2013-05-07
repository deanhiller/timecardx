package controllers;

import models.EmailToUserDbo;
import models.Token;
import models.UserDbo;

import com.alvazan.play.NoSql;

import controllers.auth.Secure;
import controllers.auth.Secure.Security;
import play.mvc.Controller;

public class Register extends Controller {
	// This is for company admin
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
		// Add self as manager for company Admin
		user.setManager(user);
		user.setAdmin(true);
		NoSql.em().put(user);

		EmailToUserDbo emailToUser = new EmailToUserDbo();
		emailToUser.setId(email);
		emailToUser.setValue(user.getId());
		NoSql.em().put(emailToUser);

		NoSql.em().flush();

		Secure.addUserToSession(user.getEmail());

		OtherStuff.company();
	}

	public static void userRegister() {
		render();
	}

	public static void userRegister2(String token) {
		Token tkn = NoSql.em().find(Token.class, token);
		String email = tkn.getEmail();
		long sendmailtime = tkn.getTime();
		long logintime = System.currentTimeMillis();
		long duration = (7 * 24 * 60 * 60);
		long interval = logintime - sendmailtime;
		validation.required(interval);
		if (interval > duration) {
			validation.addError("interval",
					"Plese request the admin to send the message again");

		} else {
			render(email);
		}
		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			Application.index();
		}
	}

	// This for employees who has recieved the mail
	public static void postUserRegister(String email, String password, String verifyPassword, String firstName, String lastName, String phone) {
		validation.required(email);
		if (password == null) {
			validation.addError("password", "Password must be supplied");
		}
		if (!password.equals(verifyPassword)) {
			validation.addError("verifyPassword", "Passwords did not match");
		}
		if (!email.contains("@"))
			validation.addError("email", "This is not a valid email");

		EmailToUserDbo existing = NoSql.em().find(EmailToUserDbo.class, email);
		if (existing == null) {
			validation.addError("email", "This email is not registered with us");
		}

		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			userRegister();
		}
		UserDbo user = NoSql.em().find(UserDbo.class, existing.getValue());
		//user.setEmail(email);
		user.setPassword(password);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setPhone(phone);
		user.setAdmin(false);
		NoSql.em().put(user);

		EmailToUserDbo ref = NoSql.em().find(EmailToUserDbo.class, user.getManager().getEmail());
		UserDbo manager = NoSql.em().find(UserDbo.class, ref.getValue());
		manager.addEmployee(user);
		NoSql.em().put(manager);

		NoSql.em().flush();
		Secure.addUserToSession(user.getEmail());
		OtherStuff.employee();
	}
}
