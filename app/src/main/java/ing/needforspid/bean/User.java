package ing.needforspid.bean;

/**
 * Created by Paolo on 30/10/17.
 */

public class User {

    private String firstname;
    private String surname;
    private String email;
    private String token;


    public User ()
    {
        firstname="";
        surname="";
        email="";
        token="";
    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
