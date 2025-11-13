package com.javaweb.entity;

import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.javaweb.entity.ChatAndCall.CallEntity;
import com.javaweb.entity.ChatAndCall.ConversationEntity;
import com.javaweb.entity.ChatAndCall.ConversationParticipantEntity;
import com.javaweb.entity.Competition.CompetitionParticipantEntity;
import com.javaweb.entity.Competition.CompetitionRegistrationEntity;
import com.javaweb.entity.Course.CourseEnrollmentEntity;
import com.javaweb.entity.Event.EventParticipantsEntity;
import com.javaweb.entity.Friend.FriendshipEntity;
import com.javaweb.entity.Payment.PaymentTransactionEntity;
import com.javaweb.entity.Post.CommentEntity;
import com.javaweb.entity.Post.CommentLikeEntity;
import com.javaweb.entity.Post.PostEntity;
import com.javaweb.entity.Post.PostLikeEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="users")
/*@NamedStoredProcedureQuery(
	    name = "UserEntity.userLogin",
	    procedureName = "user_login",
	    parameters = {
	        @StoredProcedureParameter(mode = ParameterMode.IN, name = "u_email", type = String.class),
	        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "u_id", type= Long.class),
	        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "u_password", type = String.class),
	        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "u_accountStatus", type = String.class),
	        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "u_result", type = String.class)
	    }
)*/
@DynamicInsert
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long UserID;
	@Column(name="Username", nullable = false)
	private String username;
	@Column(name="Password", nullable = false)
	private String password;
	@Column(name="Email", nullable = false)
	private String email;
	@Column(name="FullName", nullable = false)
	private String fullName;
	@Column(name="DateOfBirth")
	private String dateOfBirth;
	@Column(name="School", nullable = false)
	private String school;
	@Column(name="AccountStatus")
	private String accountStatus;
	@Column(name="Status")
	private String status;
	@Column(name="Role")
	private String role;
	@Column(name="Avatar")
	private String avatar;
	@Column(name = "Image")
	private String image;
	@Column(name="EmailVerified")
	private boolean emailVerified;
	@Column(name="Provider")
	private String provider;
	@Column(name = "CreatedAt")
	private LocalDateTime createdAt;
	@Column(name = "LastLoginAt")
	private LocalDateTime lastLoginAt;
	@Column(name = "Bio", columnDefinition = "TEXT")
	private String bio;

	@Column(name = "PhoneNumber")
	private String phoneNumber;

	@Column(name = "Address")
	private String address;

	@Column(name = "City")
	private String city;

	@Column(name = "Country")
	private String country;


	@OneToMany(mappedBy = "initiator", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<CallEntity> calls = new ArrayList<>();

	@OneToMany(mappedBy="user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<PaymentTransactionEntity> paymentTransaction = new ArrayList<>();

	@OneToMany(mappedBy="userEnrollment" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<CourseEnrollmentEntity> courseEnrollment = new ArrayList<>();

	@OneToMany(mappedBy="passkey", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<PasskeyCredentialsEntity> userPasskey = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<EventParticipantsEntity> participants = new TreeSet<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private Set<PostEntity> post = new TreeSet<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<PostLikeEntity> postlike = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<CommentEntity> comment = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<CommentLikeEntity> commentlike = new ArrayList<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private Set<FriendshipEntity> sentRequest = new HashSet<>();

	@OneToMany(mappedBy = "friend", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private Set<FriendshipEntity> receivedRequest = new HashSet<>();

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<ConversationParticipantEntity> conversationParticipant;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<ConversationEntity> conversation;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<CompetitionRegistrationEntity> registration;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<CompetitionParticipantEntity> participant;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private UserProfileEntity profile;

	@JsonBackReference
	public UserProfileEntity getProfile() {
		return profile;
	}

	public void setProfile(UserProfileEntity profile) {
		this.profile = profile;
	}

	@JsonBackReference
	public List<CompetitionParticipantEntity> getParticipant() {
		return participant;
	}

	public void setParticipant(List<CompetitionParticipantEntity> participant) {
		this.participant = participant;
	}

	@JsonBackReference
	public List<CompetitionRegistrationEntity> getRegistration() {
		return registration;
	}

	public void setRegistration(List<CompetitionRegistrationEntity> registration) {
		this.registration = registration;
	}

	@JsonBackReference
	public List<ConversationEntity> getConversation() {
		return conversation;
	}

	public void setConversation(List<ConversationEntity> conversation) {
		this.conversation = conversation;
	}

	@JsonBackReference
	public List<ConversationParticipantEntity> getConversationParticipant() {
		return conversationParticipant;
	}

	public void setConversationParticipant(List<ConversationParticipantEntity> conversationParticipant) {
		this.conversationParticipant = conversationParticipant;
	}

	@JsonBackReference
	public Set<FriendshipEntity> getReceivedRequest() {
		return receivedRequest;
	}

	@JsonBackReference
	public void setReceivedRequest(Set<FriendshipEntity> receivedRequest) {
		this.receivedRequest = receivedRequest;
	}

	public Set<FriendshipEntity> getSentRequest() {
		return sentRequest;
	}

	public void setSentRequest(Set<FriendshipEntity> sentRequest) {
		this.sentRequest = sentRequest;
	}

	@JsonBackReference
	public List<CommentLikeEntity> getCommentlike() {
		return commentlike;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setCommentlike(List<CommentLikeEntity> commentlike) {
		this.commentlike = commentlike;
	}

	public List<CommentEntity> getComment() {
		return comment;
	}

	public void setComment(List<CommentEntity> comment) {
		this.comment = comment;
	}

	@JsonBackReference
	public List<PostLikeEntity> getPostlike() {
		return postlike;
	}

	public void setPostlike(List<PostLikeEntity> postlike) {
		this.postlike = postlike;
	}

	public Set<PostEntity> getPost() {
		return post;
	}

	public void setPost(Set<PostEntity> post) {
		this.post = post;
	}

	public Set<EventParticipantsEntity> getParticipants() {
		return participants;
	}

	public void setParticipants(Set<EventParticipantsEntity> participants) {
		this.participants = participants;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public List<PasskeyCredentialsEntity> getUserPasskey() {
		return userPasskey;
	}
	public void setUserPasskey(List<PasskeyCredentialsEntity> userPasskey) {
		this.userPasskey = userPasskey;
	}
	public List<CourseEnrollmentEntity> getCourseEnrollment() {
		return courseEnrollment;
	}
	public void setCourseEnrollment(List<CourseEnrollmentEntity> courseEnrollment) {
		this.courseEnrollment = courseEnrollment;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	@JsonBackReference
	public List<PaymentTransactionEntity> getPaymentTransaction() {
		return paymentTransaction;
	}
	public void setPaymentTransaction(List<PaymentTransactionEntity> paymentTransaction) {
		this.paymentTransaction = paymentTransaction;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public List<CallEntity> getCalls() {
		return calls;
	}
	public void setCalls(List<CallEntity> calls) {
		this.calls = calls;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(LocalDateTime lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getUserID() {
		return UserID;
	}
	public void setUserID(Long userID) {
		UserID = userID;
	}

	public String getBio() {
		return bio;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}




