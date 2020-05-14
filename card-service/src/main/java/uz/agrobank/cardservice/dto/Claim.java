package uz.agrobank.cardservice.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Claim implements Serializable{
    
    private String iss;
    private String sub;
    private String aud;
    private String iat;
    private String exp;
    
}
