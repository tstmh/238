package com.stee.emasext.emaslus.utils;

import com.stee.emas.common.constants.MessageConstants;
import com.stee.emas.common.dto.CmdRespDto;
import com.stee.emasext.emaslus.enums.StatusCodeEnum;
import com.stee.emasext.emaslus.vo.ExecutingCommandVO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Queue;

/**
 * @author Wang Yu
 * Created at 2023/5/23
 */
@Slf4j
public class ExecuteCommandUtils {

    /**
     * check the executing map and then create CmdRespDto if there's a valid response
     * @param validTime To check the command is expired or not (in minute)
     * @return return a CmdRespDto if there's a valid response
     */
    public static Optional<CmdRespDto> checkExecutingMapThenCreateCmdRespDto(String controllerId,
                                                                             StatusCodeEnum statusCodeEnum,
                                                                             long validTime) {
        if (GlobalVariable.executingCommandVOQueue.containsKey(controllerId)) {
            Queue<ExecutingCommandVO> queue = GlobalVariable.executingCommandVOQueue.get(controllerId);
            ExecutingCommandVO vo;

            if (queue.isEmpty()) {
                log.info("no executing command for : {}", controllerId);
                return Optional.empty();
            }
            while ((vo = queue.poll()) != null) {
                if (!vo.getStartTime().plusMinutes(validTime)
                        .isAfter(LocalDateTime.now())) {
                    continue;
                }
                CmdRespDto cmdRespDto = new CmdRespDto();
                cmdRespDto.setExecId(vo.getExecId());
                cmdRespDto.setEquipId(vo.getEquipId());
                cmdRespDto.setCmdId(vo.getCmdId());
                cmdRespDto.setSender(vo.getSender());
                cmdRespDto.setStatus(statusCodeEnum.getCode() & 0xFF);
                return Optional.of(cmdRespDto);
            }
            if (queue.isEmpty()) {
                GlobalVariable.executingCommandVOQueue.remove(controllerId);
            }

        }
        return Optional.empty();
    }
}
