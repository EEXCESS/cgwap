package cgwap.entities;

import java.sql.Timestamp;
import java.util.Date;

import cgwap.enums.UserLevel;
import cgwap.util.database.KeyGenerator;

/**
 * Data Transfer Object which represents a user account.
 */
public class User implements Comparable<User> {

    private int id;
    private String email;
    private String password = null;
//    private boolean validEmail = false;
    private boolean isAdmin = false;
    private boolean isActive;
    private String nickname;
    private UserLevel level = UserLevel.Questling;
    private int xp;

    private Date registered;
    
    /**
     * The default constructor.
     */
    public User() {

    }

    /**
     * Constructor with parameter.
     * 
     * @param id
     *            the users identifier
     */
    public User(int id) {
        this.id = id;
    }
    
    public User(int id, String email, String password, boolean isAdmin, String nickname, UserLevel level, int xp) {
        this.id = id;
        this.email = email;
        this.setPassword(password);
        this.isAdmin = isAdmin;
        this.nickname = nickname;
        this.level = level;
        this.xp = xp;
    }

    /**
     * Creates a User filled only with an email address.
     * 
     * @param email
     *            the email of the user
     */
    public User(String email) {
        this.email = email;
    }



    /**
     * @return the users identifier
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            the users identifier
     */
    public void setId(int id) {
        this.id = id;
    }

   

    /**
     * @return the users email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the users email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the users password as a hash
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the users password which will be hashed before saving it
     */
    public void setPassword(String password) {
        this.password = KeyGenerator.hashUserPassword(password);
    }

    /**
     * @param password
     *            the hashed users password
     */
    public void setHashedPassword(String password) {
        this.password = password;
    }

    /**
     * @return true if the user is an administrator
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * @param isAdmin
     *            - flag if the user is a administrator
     */
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * @return true if the user is an administrator
     */
    public boolean getIsAdmin() {
        return isAdmin;
    }

    /**
     * @param isAdmin
     *            the isAdmin to set
     */
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    // *************************************************
    // HELPER METHODES
    // *************************************************

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (getClass() != other.getClass()) {
            return false;
        }
        User otherUser = (User) other;

        if (getId() != otherUser.getId()) {
            return false;
        }
        return true;
    }

    public int compareTo(User other) {
        int result;
        if (this.equals(other)) {
            result = 0;
        }
        else {
            result = this.email.toLowerCase().compareTo(other.email.toLowerCase());
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 13 + getId();
        hash = hash * 31 + getEmail().hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("USERID: ");
        builder.append(getId());
        builder.append("; Email: ");
        builder.append(getEmail());
        return builder.toString();
    }

    public UserLevel getUserLevel() {
        return level;
    }
    
    /**
     * Returns level as String.
     * @return
     * userLevel as String
     */
    public String getLevel() {
        return level.toString();
    }


    public void setLevel(UserLevel level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
    
    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Timestamp timestamp) {
        this.registered = timestamp;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }


}
