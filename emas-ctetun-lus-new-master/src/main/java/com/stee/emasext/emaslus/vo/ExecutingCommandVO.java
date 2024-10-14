package com.stee.emasext.emaslus.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Wang Yu
 * Created at 2023/5/17
 * This Java Bean is ONLY used for managing execting commands
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutingCommandVO {
    private String equipId;
    private String equipType;
    private String execId;
    private String cmdId;
    private String sender;
    private LocalDateTime startTime;
}
