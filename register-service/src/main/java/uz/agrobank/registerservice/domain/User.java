package uz.agrobank.registerservice.domain;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Document(collection = "user")
public class User implements Serializable {

    @Id
    private String id;
    private String firstname;
    private String lastname;
    private String phone;
    private String password;


    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
