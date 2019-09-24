package com.acxiom.ams.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Fermi.Tang
 * @Date: Created in 4:26 PM 10/9/2018
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReceiveMessageDTO {
    private String messageTitle;
    private String messageContent;
    private String sendType;
    private String messageType;
    private Condition condition;
}
