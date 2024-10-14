/* Copyright © 2010, ST Electronics Info-comm Systems PTE. LTD All rights reserved.
 *
 * This software is confidential and proprietary property of 
 * ST Electronics (Info-comm Systems) PTE. LTD.
 * The user shall not disclose the contents of this software and shall
 * only use it in accordance with the terms and conditions stated in
 * the contract or license agreement with ST Electronics Info-comm Systems PTE. LTD.
 */

/**
 * <p>Title: TT238 Project</p>
 * <p>Description : Aggregate Information</p>
 * <p>Holds the groups information</p>
 * <p>Copyright: Copyright (c) 2019 </p>
 * <p>Company:STEE-InfoComm</p>
 * @author Grace
 * @since Jul 18, 2019
 * @version 1.0
 */

package com.stee.emas.cmh.tpnd.common;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class SiteSupportPair {
	private String mdb = ""; // MDB ID
	private String telco = ""; // Telco ID
	private String pair = ""; // Pair ID
	private Date startDate;

	public SiteSupportPair() {
		mdb = ""; // MDB id
		telco = ""; // Telco id
		pair = ""; // Pair ID
		startDate = new Date();
	}
	public SiteSupportPair(String mdb, String telco, Date date) {
		this.startDate = date;
		this.mdb = mdb;
		this.telco = telco;
		processPairID();
	}

	public String getMdb() {
		return mdb;
	}

	public String getTelco() {
		return telco;
	}

	public String getPair() {
		return pair;
	}

	public void setMdb(String mdb) {
		this.mdb = mdb;
		processPairID();
	}

	public void setTelco(String telco) {
		this.telco = telco;
		processPairID();
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	private void processPairID() {
		if (!StringUtils.isBlank(this.mdb) && !StringUtils.isBlank(this.telco)) {
			this.pair = TPNDConstants.TPN_EQUIP_HEADER + this.mdb.substring(4) + TPNDConstants.MSG_JOINT
					+ this.telco.substring(8);
		}
	}
}