package uz.agrobank.smsservice.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Document(collection = "message")
public class Message implements Serializable {

    @Id
    private String id;
    private String phone;
    private String code;
    private Date createdAt;

    public Message(String phone, String code) {
        this.phone = phone;
        this.code = code;
        this.createdAt = new Date();
    }
}
