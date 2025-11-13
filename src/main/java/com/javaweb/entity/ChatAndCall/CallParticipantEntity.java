package com.javaweb.entity.ChatAndCall;

import com.javaweb.entity.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "callparticipants")
public class CallParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CallParticipantID")
    private Long callParticipantID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callID")
    private CallEntity call;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID")
    private UserEntity user;

    @Column(name = "JoinTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date joinTime;

    @Column(name = "LeaveTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date leaveTime;

    @Column(name = "Status", length = 20)
    private String status;

    @Column(name = "DeviceInfo", length = 255)
    private String deviceInfo;

    @Column(name = "NetworkQuality", length = 20)
    private String networkQuality;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public CallEntity getCall() {
        return call;
    }

    public void setCall(CallEntity call) {
        this.call = call;
    }

    public Long getCallParticipantID() {
        return callParticipantID;
    }

    public void setCallParticipantID(Long callParticipantID) {
        this.callParticipantID = callParticipantID;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Date getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Date leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getNetworkQuality() {
        return networkQuality;
    }

    public void setNetworkQuality(String networkQuality) {
        this.networkQuality = networkQuality;
    }
}