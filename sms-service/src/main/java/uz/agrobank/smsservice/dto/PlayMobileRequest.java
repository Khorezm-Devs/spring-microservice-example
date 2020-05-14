package uz.agrobank.smsservice.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class PlayMobileRequest {
    private List<PlayMobileMessageDTO> messages;
}
