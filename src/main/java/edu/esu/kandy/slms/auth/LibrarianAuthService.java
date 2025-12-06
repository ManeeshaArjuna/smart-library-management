package edu.esu.kandy.slms.auth;

public class LibrarianAuthService {

    private final String username = "librarian";
    private final String password = "lib123";

    public boolean authenticate(String user, String pass) {
        if (user == null || pass == null) return false;
        return username.equals(user.trim()) && password.equals(pass.trim());
    }
}
