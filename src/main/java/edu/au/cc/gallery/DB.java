package edu.au.cc.gallery;

import java.util.*;

import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.json.JSONArray;
import org.json.JSONObject;


public class DB {

    private static final String dbUrl = "jdbc:postgresql://m5-ig-rds.cy7qnq8x0c88.us-east-2.rds.amazonaws.com/image-gallery";
<<<<<<< HEAD
=======
//    private static final String dbUrl = "jdbc:postgresql://image-gallery.cy7qnq8x0c88.us-east-2.rds.amazonaws.com/image_gallery1";
>>>>>>> 678047f29b10e75b1e2e645513e36ad7449516de
    private Connection connection;

    private JSONObject getSecret() {
    String s = Secrets.getSecretImageGallery();
    return new JSONObject(s);
    }

    private String getPassword(JSONObject secret) {
   return secret.getString("password"); 

   }

/*    private String getPassword(){
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/sql-passwd"))) {
            String result = br.readLine();
            return result;
        }
        catch (IOException ex){
            System.err.println("Error opening password file.");
            System.exit(1);
        }
        return null;
    }
*/

    public void connect() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
	    JSONObject secret = getSecret();
            connection = DriverManager.getConnection(dbUrl, "image-gallery", "simple");
        }
        catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
    }

   public ResultSet execute(String query) throws SQLException {
      PreparedStatement stmt = connection.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      return rs;
   }

   public void execute(String query, String[] values) throws SQLException {
      PreparedStatement stmt = connection.prepareStatement(query);
     for (int i = 0; i < values.length; i++)
         stmt.setString(i+1, values[i]);
      stmt.execute();
  }

    public ResultSet executeQuery(String query) throws SQLException {
      PreparedStatement stmt = connection.prepareStatement(query);
      ResultSet rs = stmt.executeQuery();
      return rs;
   }

   public ResultSet executeQuery(String query, String[] values) throws SQLException {
      PreparedStatement stmt = connection.prepareStatement(query);
     for (int i = 0; i < values.length; i++)
         stmt.setString(i+1, values[i]);
      return stmt.executeQuery();
  }



   public void close() throws SQLException {
       connection.close();
   }


    public ArrayList<String> listUsers() throws SQLException {
    ArrayList<String> list = new ArrayList<>();
    connect();
    PreparedStatement stmt = connection.prepareStatement("select * from users");
    ResultSet rs = stmt.executeQuery();
    while (rs.next()) {
    list.add(rs.getString(1));
    }
	return list;
    }

    public void listUser() throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("select * from users");
        ResultSet rs = stmt.executeQuery();
        System.out.printf("%-12s %-12s %-12s\n", "username", "password", "full name");
        System.out.println("------------------------------------");
        while (rs.next()) {
            System.out.printf("%-12s %-12s %-12s\n", rs.getString(1),rs.getString(2),rs.getString(3));
        }
        System.out.println();
        rs.close();
    }

    public void addUser() throws SQLException {
        Scanner input = new Scanner(System.in);
        String usr, pw, fn = "";
        System.out.print("Adding User, Enter the following:\n");
        System.out.println("1) Username:");
        usr = input.next();
        System.out.println("2) Password:");
        pw = input.next();
        Scanner add = new Scanner(System.in);
        System.out.println("3) Full name:\n");
        fn = add.nextLine();
        PreparedStatement stmt = connection.prepareStatement("select count(*) from users  where username = " + "'"+usr+"'");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            int dup = rs.getInt(1);
            if (dup==0){
                PreparedStatement stmt1 = connection.prepareStatement("insert into users values (?,?,?)");
                stmt1.setString(1, usr);
                stmt1.setString(2, pw);
                stmt1.setString(3, fn);
                int rowsAdded = stmt1.executeUpdate();
                System.out.println(rowsAdded + " user added\n");
            }
            else {
                System.out.println("Error: user with username " + usr + " already exists\n");
            }
        }
    }

    public void editUser() throws SQLException {
        Scanner input = new Scanner(System.in);
        String editUser, editPassword, editFullname;
        String newPassword, newFullname = "test";
        System.out.println("Username to edit: ");
        editUser = input.next();
        PreparedStatement stmt = connection.prepareStatement("select count(*) from users  where username = " + "'"+editUser+"'");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()){
            int dupUser = rs.getInt(1);
            if (dupUser==0) {
                System.out.println("No such user\n");
            }
            else {
                System.out.println("New password (press enter to keep current):");
                Scanner scanner1 = new Scanner(System.in);
                if (scanner1.nextLine().equals("")){
                    System.out.println("New full name (press enter to keep current):");
                    Scanner scanner2 = new Scanner(System.in);
                    String temp = scanner2.nextLine();
                    if (temp.equals("")){
                        System.out.println("No change for " + editUser);
                    }
                    else{
                        Scanner edit = new Scanner(System.in);
                        newFullname = edit.nextLine();
                        PreparedStatement stmt1 = connection.prepareStatement("update users set full_name='"+newFullname+"' where username='"+editUser+"'" );
                        int rowsAdded = stmt1.executeUpdate();
                        System.out.println(rowsAdded + " user full name  edited\n");

                    }
                }
                else {
                    Scanner pass = new Scanner(System.in);
                    newPassword = pass.next();
                    System.out.println("New full name (press enter to keep current):");
                    Scanner pass1 = new Scanner(System.in);
                    if (pass1.nextLine().equals("")){
                        PreparedStatement stmt1 = connection.prepareStatement("update users set password='"+newPassword+"' where username='"+editUser+"'" );
                        int rowsAdded = stmt1.executeUpdate();
                        System.out.println(rowsAdded + " user password edited\n");

                    }
                    else {
                        newFullname = pass1.nextLine();
                        PreparedStatement stmt1 = connection.prepareStatement("update users set password='"+newPassword
                                + "',full_name='"+newFullname+ "' where username='"+editUser+"'" );
                        int rowsAdded = stmt1.executeUpdate();
                        System.out.println(rowsAdded + " user password and full name edited\n");
                    }
                }
            }
        }
    }

    public void deleteUser() throws SQLException {
        Scanner input = new Scanner(System.in);
        String deleteUser = "";
        System.out.println("Enter username to delete");
        deleteUser = input.next();
        PreparedStatement stmt2 = connection.prepareStatement("select count(*) from users  where username = " + "'"+deleteUser+"'");
        ResultSet rs2 = stmt2.executeQuery();
        while (rs2.next()){
            int exists = rs2.getInt(1);
            if (exists==0) {
                System.out.println("No such user, nothing to delete\n");
            }
            else{
                System.out.println("Are you sure you want to delete user "+deleteUser+ " (Enter Yes or No)");
                Scanner del = new Scanner(System.in);
                String confirm = input.next().toUpperCase();
                if (confirm.equals("YES")) {
                    PreparedStatement stmt3 = connection.prepareStatement("Delete from users  where username = " + "'"+deleteUser+"'");
                    int rowsDeleted = stmt3.executeUpdate();
                    System.out.println("Deleted\n");
                }
                else{
                    System.out.println("Exiting");
                }
            }


        }
    }


    public void execute() throws SQLException {
        int choice = 0;
        while (choice != 5){
            System.out.println("----------------------------");
            System.out.println("Select an option from below:");
            System.out.println("1) List users");
            System.out.println("2) Add user");
            System.out.println("3) Edit Users");
            System.out.println("4) Delete user");
            System.out.println("5) Quit");
            System.out.println("----------------------------\n");
            Scanner input = new Scanner(System.in);
            choice = input.nextInt();
            if (choice==1) {
             listUser();
            }
            else if (choice==2) {
                addUser();
            }
            else if (choice==3) {
                editUser();
            }
            else if (choice==4) {
                deleteUser();
            }
	    else if (choice==5) {
		System.out.println("Goodbye\n");
	    }
            else {
                System.out.println("Invalid input.  Enter 1 - 5");
            }
        }
    }

    public static void demo() throws Exception {
        DB db = new DB();
        db.connect();
        db.execute();
    }
}
