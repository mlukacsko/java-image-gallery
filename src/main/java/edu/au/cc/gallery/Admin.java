package edu.au.cc.gallery;

import edu.au.cc.gallery.UserDAO;
import edu.au.cc.gallery.Postgres;
import edu.au.cc.gallery.User;
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

        private String sessionDemo(Request req, Response resp) {
		if (req.session().isNew()) {
			req.session().attribute("owner", "fred");
			req.session().attribute("foo", "bar");
		}
		else {
		}
		return "stored";

        }

	private String debugSession(Request req, Response resp) {
		StringBuffer sb = new StringBuffer();
		for(String key: req.session().attributes()) {
			sb.append(key+" --> "+req.session().attribute(key)+"<br />");
		}
		return sb.toString();
	}

	private String login(Request req, Response resp) {
		Map<String, Object> model = new HashMap<String, Object>();
                return new HandlebarsTemplateEngine()
                        .render(new ModelAndView(model, "login.hbs"));
	}

	private String loginPost(Request req, Response resp) {
		try {
			String username = req.queryParams("username");
			User u = getUserDAO().getUserByUsername(username);
			if (u == null || !u.getPassword().equals(req.queryParams("password")))
				return "Invalid username or password, go back to try again";
			req.session().attribute("user", username);
			resp.redirect("/debugSession");
			return "";
		} catch (Exception e) {
			return e.getMessage();
		}
        }

	private boolean isAdmin(String username) {
		return username != null && username.equals("Emilia");
	}

	private void checkAdmin(Request req, Response resp) {
		if (!isAdmin(req.session().attribute("user"))) {
			resp.redirect("/login");
			halt();
		}
	}

	public String mainMenu(Request req, Response resp) {
		Map<String, Object> model = new HashMap<String, Object>();
                return new HandlebarsTemplateEngine()
                        .render(new ModelAndView(model, "mainMenu.hbs"));
	}

/*	public void uploadImage(Request req, Response resp) {
	
	}

	public String viewImage(Request req, Response resp) {
                return "";
        }
*/

	public void addRoutes() {
		get("/admin/users", (req, res) -> listUsers());
		get("/admin/addUser/:username/:password/:fullName",
			(req, res) -> addUser(req.params("username"), req.params(":password"), req.params(":fullName"), res));
		get("/admin/deleteUser/:username", (req, res) -> deleteUser(req, res));
		get("/admin/deleteUserExec/:username", (req, res) -> deleteUserExec(req, res));
	        get("/sessionDemo", (req, res) -> sessionDemo(req, res));
                get("/login", (req, res) -> login(req, res));
                before("/admin/*", (request, response) -> checkAdmin(request, response));
                post("/login", (req, res) -> loginPost(req, res));
		get("/debugSession", (req, res) -> debugSession(req, res));
		get("/", (req, res) -> mainMenu(req, res));
  //              post("/uploadImage", (req, res) -> uploadImage(req, res));
//		get("/viewImage", (req, res) -> viewImage(req, res));
	}

}
