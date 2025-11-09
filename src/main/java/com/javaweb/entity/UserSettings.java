package com.javaweb.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usersettings")
@DynamicInsert
@DynamicUpdate
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SettingID")
    private Long settingID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "Theme", length = 20)
    private String theme;

    @Column(name = "NotificationEmail")
    private Boolean notificationEmail;

    @Column(name = "NotificationPush")
    private Boolean notificationPush;

    @Column(name = "NotificationInApp")
    private Boolean notificationInApp;

    @Column(name = "Language", length = 10)
    private String language;

    @Column(name = "TimeZone", length = 50)
    private String timeZone;

    @Column(name = "ProfileVisibility", length = 20)
    private String profileVisibility;

    @Column(name = "LastLoggedIn")
    private LocalDateTime lastLoggedIn;

    @UpdateTimestamp
    @Column(name = "LastUpdated", updatable = false) // <--- Đã BỎ insertable = false
    private LocalDateTime lastUpdated;

    // !! Khởi tạo Constructor để khớp với các giá trị DEFAULT của DB !!
    public UserSettings() {
        this.theme = "light";
        this.notificationEmail = true; // DB default là 1 (TINYINT)
        this.notificationPush = true;  // DB default là 1
        this.notificationInApp = true; // DB default là 1
        this.language = "vi-VN";
        this.timeZone = "Asia/Ho_Chi_Minh";
        this.profileVisibility = "public";
        this.lastLoggedIn = null; // Cột YES NULL (không cần giá trị)
        // LastUpdated sẽ được DB/Hibernate xử lý
    }

    // Getters and Setters...
    // (Giữ nguyên các getters/setters đã có)
    public Long getSettingID() {
        return settingID;
    }

    public void setSettingID(Long settingID) {
        this.settingID = settingID;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Boolean getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(Boolean notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public Boolean getNotificationPush() {
        return notificationPush;
    }

    public void setNotificationPush(Boolean notificationPush) {
        this.notificationPush = notificationPush;
    }

    public Boolean getNotificationInApp() {
        return notificationInApp;
    }

    public void setNotificationInApp(Boolean notificationInApp) {
        this.notificationInApp = notificationInApp;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public LocalDateTime getLastLoggedIn() {
        return lastLoggedIn;
    }

    public void setLastLoggedIn(LocalDateTime lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}