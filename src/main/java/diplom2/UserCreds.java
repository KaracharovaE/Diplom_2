package diplom2;

public class UserCreds {

    private String email;
    private String password;

    public UserCreds(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static UserCreds credsFromUser(User user) {
        return new UserCreds(user.getEmail(), user.getPassword());
    }
}
