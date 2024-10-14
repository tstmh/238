package com.stee.pasystem.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Wang Yu
 * Created at 2023/5/15
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class PaJmsMessage<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = -6187524644295165634L;

    private T data;
    private String correlationId;
}
