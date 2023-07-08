package service;


import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Hello implements Serializable {
    private String message;
    private String descrition;
}
