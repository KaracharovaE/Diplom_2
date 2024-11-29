package diplom2;

public class User {

    private String email;
    private String password;
    private String name;
    String token;

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public User withEmail(String email) {
        this.email = email;
        return this;
    }

    public User withPassword(String password) {
        this.password = password;
        return this;
    }

    public User withName(String name) {
        this.name = name;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User withEmptyPassword() {
        this.password = "";
        return this;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}