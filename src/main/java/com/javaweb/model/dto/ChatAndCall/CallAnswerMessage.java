package com.javaweb.model.dto.ChatAndCall;

import lombok.Data;

@Data
public class CallAnswerMessage {
    private Long callID;
    private Long respondentID;
    private String respondentName;
    private String respondentPicture;
}