package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.play.NoSql;

import models.EmailToUserDbo;
import models.UserDbo;
import models.TimeCardDbo;
import models.CompanyDbo;

import controllers.auth.Check;
import controllers.auth.Secure;
import controllers.auth.Secure.Security;
import play.Play;
import play.data.validation.Required;
import play.libs.Crypto;
import play.libs.Time;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Scope.Session;

@With(Secure.class)
public class OtherStuff extends Controller {

	private static final Logger log = LoggerFactory.getLogger(OtherStuff.class);

	public static void company() {
		UserDbo user = Utility.fetchUser();
		CompanyDbo company = user.getCompany();
		log.info("User = " + user +" and Company = " + company); 
		render(user, company);
	}


	public static void addCompany() {
		render();
	}

	public static void companyDetails() {
		UserDbo user = Utility.fetchUser();
		CompanyDbo company = user.getCompany();
		log.info("User = " + user.getEmail() +" and Company = " + company);
		List<UserDbo> users = null;
		if (company != null)
			users = company.getUsers();
		render(user, company, users);
	}

	public static void postAddition(String name, String address, String phone, String detail) throws Throwable {
		validation.required(name);
		UserDbo user = Utility.fetchUser();
	
		if(validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
	        addCompany();
		}
		CompanyDbo company = new CompanyDbo();
		company.setName(name);
		company.setAddress(address);
		company.setPhoneNumber(phone);
		company.setDescription(detail);
		company.addUser(user);
		
		user.setCompany(company);
		
		NoSql.em().put(company);
		NoSql.em().put(user);
		
		NoSql.em().flush();
		company();
	}

	public static void addUser() {
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();
		log.info("Adding users by Admin = " + admin.getEmail()
				+ " and Company = " + company.getName());
		List<UserDbo> users = company.getUsers();

		render(admin, company, users);
	}

	public static void postUserAddition(String useremail, String manager) throws Throwable {
		validation.required(useremail);

		if (!useremail.contains("@"))
			validation.addError("email", "This is not a valid email");

		EmailToUserDbo existing = NoSql.em().find(EmailToUserDbo.class,
				useremail);
		if (existing != null) {
			validation.addError("email", "This email already exists");
		}

		if (validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			addUser();
		}
		UserDbo admin = Utility.fetchUser();
		CompanyDbo company = admin.getCompany();

		UserDbo user = new UserDbo();
		user.setEmail(useremail);
		user.setCompany(company);
		if (manager == null) {
			// If there is no manager, add the current user as Manager
			user.setManager(admin);
		} else {
			EmailToUserDbo ref = NoSql.em().find(EmailToUserDbo.class, manager);
			UserDbo adminDbo = NoSql.em().find(UserDbo.class, ref.getValue());
			user.setManager(adminDbo);
		}

		NoSql.em().put(user);

		EmailToUserDbo emailToUser = new EmailToUserDbo();
		emailToUser.setId(useremail);
		emailToUser.setValue(user.getId());
		NoSql.em().put(emailToUser);

		company.addUser(user);
		NoSql.em().put(company);

		NoSql.em().flush();

		// Utility.sendEmail(useremail, company.getName());
		companyDetails();
	}

	public static void success() {
		render();
	}

	public static void cancel() {
		render();
	}
}
