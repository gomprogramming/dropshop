package dropshop.model.entities;

public class User{
    public enum Role {
    ADMIN,
    CUSTOMER
}
  
    private int id;
    private String username;
    private String hashpassword;
    private String email;
    private String address;
    private Role role;


    public User(int id, String username, String hashpassword, String email, String address, Role role) {
        this.id = id;
        this.username = username;
        this.hashpassword = hashpassword;
        this.email = email;
        this.address = address;
        this.role = role;
    }
    public int getId(){
        return id;
    }
    public String getUsername(){
        return username;
    }
    public String getEmail(){
        return email;
    }
    public String getAddress(){
        return address;
    }
    public Role getRole(){
        return role;
    }       
    public boolean checkPassword(String hashpassword){
        return this.hashpassword.equals(hashpassword);
    }
    public void changePassword(String currentHash, String newHash) {
        if (!checkPassword(currentHash)) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
    this.hashpassword = newHash;
    }

    public boolean isAdmin(){
        if(this.role == Role.ADMIN){
            return true;
        }else{
            return false;
        }
    }
     
}