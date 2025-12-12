package edu.esu.kandy.slms.auth;

public class LibrarianAuthService {

    // Hardcoded credentials for demonstration purposes
    private final String username = "admin";
    private final String password = "admin123";

    public boolean authenticate(String user, String pass) {
        if (user == null || pass == null) return false;
        return username.equals(user.trim()) && password.equals(pass.trim());
    }
}
