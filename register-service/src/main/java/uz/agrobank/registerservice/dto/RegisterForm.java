package uz.agrobank.registerservice.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class RegisterForm implements Serializable {
    private String phone;
    private String password;
    private String verification;
}
