package uz.agrobank.smsservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PlayMobileSmsDTO {
    private String originator;
    private PlayMobileSmsContentDTO content;
}
