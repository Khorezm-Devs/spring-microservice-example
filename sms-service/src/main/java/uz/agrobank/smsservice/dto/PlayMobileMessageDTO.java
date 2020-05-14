package uz.agrobank.smsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayMobileMessageDTO {
    private String recipient;
    private String messageId;
    private PlayMobileSmsDTO sms;

    public PlayMobileMessageDTO() {
    }

    public PlayMobileMessageDTO(String recipient, String messageId, PlayMobileSmsDTO sms) {
        this.recipient = recipient;
        this.messageId = messageId;
        this.sms = sms;
    }

    @JsonProperty("message-id")
    public String getMessageId() {
        return messageId;
    }

    @JsonProperty("message-id")
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public PlayMobileSmsDTO getSms() {
        return sms;
    }

    public void setSms(PlayMobileSmsDTO sms) {
        this.sms = sms;
    }
}
