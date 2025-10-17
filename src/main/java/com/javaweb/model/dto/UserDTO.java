package com.javaweb.model.dto;

public class UserDTO {
	private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String dateOfBirth;
    private String school;
    private String accountStatus;

    public UserDTO() {}

    public UserDTO(Long id, String username, String email, String password, String fullName, String dateOfBirth, String school) {
    	this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.school = school;
    }
    
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    
}
