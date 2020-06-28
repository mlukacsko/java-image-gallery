package edu.au.cc.gallery;

import edu.au.cc.gallery.UserDAO;
import static spark.Spark.*;
import spark.Request;
import spark.Response;
import java.util.*;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;



public class Admin {
	private static UserDAO getUserDAO() throws Exception {
		return Postgres.getUserDAO();
	}

	private String deleteUser(Request req, Response res) {
		Map<String, Object> model = new HashMap<>();
		model.put("title", "Delete User");
		model.put("message", "Are you sure you want to delete this user?");
		model.put("onYes", "/admin/deleteUserExec/"+req.params(":username"));
		model.put("onNo", "/admin/users");
		return new HandlebarsTemplateEngine()
			.render(new ModelAndView(model, "confirm.hbs"));
	}

	private String deleteUserExec(Request req, Response resp) {
		try {
			UserDAO dao = getUserDAO();
			dao.deleteUser(req.params(":username"));
			resp.redirect("/admin/users");
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

	private static String listUsers() {
		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("users", getUserDAO().getUsers());
			return new HandlebarsTemplateEngine()
				.render(new ModelAndView(model, "users.hbs"));
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	private static String addUser(String username, String password, String fullName, Response r) {
		try {
			UserDAO dao = getUserDAO();
			dao.addUser(new User(username, password, fullName));
			r.redirect("/users");
			return "";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public void addRoutes() {
		get("/admin/users", (req, res) -> listUsers());
		get("/admin/addUser/:username/:password/:fullName",
			(req, res) -> addUser(req.params("username"), req.params(":password"), req.params(":fullName"), res));
		get("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));
		get("/admin/deleteUserExec/:username", (req, res) -> deleteUserExec(req, res));
	
	}

}
