package com.javaweb.model.dto;

public class UserDTO {
<<<<<<< HEAD
	private Long id;
    private String username;
    private String email;
=======
	private Long userID;
    private String username;
    private String email;
    private String avatar;
    private String image;
    private String status;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    private String password;
    private String fullName;
    private String dateOfBirth;
    private String school;
    private String accountStatus;

    public UserDTO() {}

<<<<<<< HEAD
    public UserDTO(Long id, String username, String email, String password, String fullName, String dateOfBirth, String school) {
    	this.id = id;
=======
    public UserDTO(Long userID, String username, String email, String password, String fullName, String dateOfBirth, String school) {
    	this.userID = userID;
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.school = school;
    }
<<<<<<< HEAD
    
=======

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
    public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() { 
        return fullName;
    }
    public void setFullName(String fullName) {  
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSchool() {
        return school;
    }
    public void setSchool(String school) {
        this.school = school;
    }

<<<<<<< HEAD
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    
=======
    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
>>>>>>> 923e3092c89befcef8151ac54e3c33b5f467d36c
}
