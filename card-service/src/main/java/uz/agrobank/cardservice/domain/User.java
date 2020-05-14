package uz.agrobank.cardservice.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


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
}
