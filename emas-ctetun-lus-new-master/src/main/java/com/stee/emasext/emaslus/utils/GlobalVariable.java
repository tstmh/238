package com.stee.emasext.emaslus.utils;

import com.stee.emasext.emaslus.vo.ExecutingCommandVO;
import io.netty.channel.Channel;

import java.util.*;

public class GlobalVariable {
    // to manage the collection of Socket Channel, key is ip address
    public static Map<String, Channel> channelMap = Collections.synchronizedMap(new HashMap<>());
    // to manage the coming commands, the key is controllerId
    public static Map<String, Queue<ExecutingCommandVO>> executingCommandVOQueue =
            Collections.synchronizedMap(new HashMap<>());

    // to manage the ControllerId and IP mapping. <ControllerId, IP>
    public static Map<String, String> ControllerIdIpMap = new HashMap<>();
    // <IP, ControllerId>
    public static Map<String, String> IpAndControllerIdMap = new HashMap<>();

}
