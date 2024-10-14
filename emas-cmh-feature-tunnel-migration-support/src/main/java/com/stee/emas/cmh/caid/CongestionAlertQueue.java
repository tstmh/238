package com.stee.emas.cmh.caid;

import java.util.LinkedList;

//import org.slf4j.Logger;

import com.stee.emas.common.dto.TrafficAlertDto;

/**
 * <p>Title: EMAS Enhancement Project</p>
 * <p>Description : Congestion Alert Queue</p>
 * <p>Queue class for holding the Alert once the congestion is confirmed  
 * <p>Copyright: Copyright (c) 2012 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Scindia
 * @since Oct 23, 2013
 * @version 1.0
 *
 */

public class CongestionAlertQueue {

	//private static Logger logger = LoggerFactory.getLogger(CongestionAlertQueue.class);
	
	private static CongestionAlertQueue congestionAlertQueue = new CongestionAlertQueue();

	private LinkedList<TrafficAlertDto> congestionAlertLinkedList;

	/**
	 * Constructor
	 */
	private CongestionAlertQueue() {
		//logger.info("Constructor CongestionAlertQueue Called ...");
		congestionAlertLinkedList = new LinkedList<TrafficAlertDto>();
	}

	/**
	 * Get instance of CongestionAlertQueue
	 * @return CongestionAlertQueue
	 */
	public static CongestionAlertQueue getInstance() {
		return congestionAlertQueue;
	}

	/**
	 * Add TrafficAlertObj into CongestionAlertQueue
	 * @param trafficAlert
	 * @return boolean
	 */
	public synchronized boolean add(TrafficAlertDto trafficAlertDto) {
		boolean result = congestionAlertLinkedList.add(trafficAlertDto);
		if (1 == congestionAlertLinkedList.size()) {
			notifyAll();
		}
		return result;
	}

	/**
	 * Get TrafficAlertObj from CongestionAlertQueue
	 * @return TrafficAlertObj
	 */
	public synchronized TrafficAlertDto get() {
		if (0 == congestionAlertLinkedList.size()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		return congestionAlertLinkedList.removeFirst();
	}

	/**
	 * Check if CongestionAlertQueue contains the TrafficAlertObj
	 * @param trafficAlert
	 * @return boolean
	 */
	public synchronized boolean contains(TrafficAlertDto trafficAlert) {
		return congestionAlertLinkedList.contains(trafficAlert);
	}

	/**
	 * Remove all TrafficAlertObj from CongestionAlertQueue
	 */
	public synchronized void removeAll() {
		congestionAlertLinkedList.clear();
	}

	/**
	 * Get the size of CongestionAlertQueue
	 * @return int
	 */
	public synchronized int size() {
		return congestionAlertLinkedList.size();
	}

	/* (non-Javadoc)
	 * Convert CongestionAlertQueue into String
	 * @see java.lang.Object#toString()
	 */
	public synchronized String toString() {
		StringBuffer strObject = new StringBuffer("[ CongestionAlertQueue { ");
		strObject.append("\n\tAlerts = " + congestionAlertLinkedList.toString()
				+ ",");
		return strObject.toString();
	}

}
