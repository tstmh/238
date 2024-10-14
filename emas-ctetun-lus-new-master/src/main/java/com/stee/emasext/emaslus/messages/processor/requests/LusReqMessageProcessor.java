package com.stee.emasext.emaslus.messages.processor.requests;

import com.stee.emas.common.tunnel.TunnelRemoteDto;
import com.stee.emasext.emaslus.dao.interfaces.primary.HistCtrlRecordRepository;
import com.stee.emasext.emaslus.entities.primary.HistCtrlRecord;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author Wang Yu
 * Created at 2023/5/3
 * When receive The Command from AW (CMH), do process
 */
public interface LusReqMessageProcessor<T extends TunnelRemoteDto> {
    void process(T message);

    default void saveHistControl(HistCtrlRecordRepository recordRepository, T message) {
        HistCtrlRecord histCtrlRecord = new HistCtrlRecord();
        histCtrlRecord.setEquipId(message.getEquipId());
        histCtrlRecord.setSender(message.getSender());
        histCtrlRecord.setCmdId(message.getCmdId());
        histCtrlRecord.setExecId(message.getExecId());
        histCtrlRecord.setCmdContent(message.toString());
        histCtrlRecord.setReceivedDate(LocalDateTime.now());
        recordRepository.save(histCtrlRecord);
    }

    default void doProcess(HistCtrlRecordRepository recordRepository, T message) {
        saveHistControl(recordRepository, message);
        process(message);
    }
}
