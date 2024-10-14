package com.stee.emas.cmh.tpnd.common;

public class TPNDConstants {
	// for the enabling/disabling of features
	public static final boolean TPNDFlag		= true; // whether TPND is switched on
	public static final boolean WithTPN			= true; // whether TPN (virtual pair) is involved
	public static final boolean FinalVersion	= false; // whether individual alarms that are grouped will be removed

	public static final String TPND_TICSS_SYSTEM			= "ticss";
	public static final String TPND_IDSS_SYSTEM				= "idss"; 	// Added on 11/04/22 for IDSS implementation into TPND
	public static final String TPND							= "tpnd";
	public static final String TPND_MDB_TYPE				= "mdb";
	public static final String TPND_TELCO_TYPE				= "tel";
	public static final String TPND_PAIR_TYPE				= "tpn";
	public static final String TPND_RECOVERY				= "recovery";
	
	
	public static final String TPN_EQUIP_HEADER			= "tpn_";
	public static final String MDB_EQUIP_HEADER			= "mdb_";
	public static final String TEL_EQUIP_HEADER			= "tel_";
	public static final String MSG_HEADER				= "emas_";
	public static final String MSG_CODE					= "_1";
	public static final String MSG_JOINT				= "_";
	
	public static final long TPND_1_SECOND				= 1000;
	public static final long TPND_1_MINUTE				= 60000;
	public static final long TPND_1_HOUR				= 3600000;
	
	public static final String[] OPE_TPND_TYPE_ARRAY 		= {"mdb","tel"};
	public static final String[] TICSS_TYPE_ARRAY			= {"tip","tsp","tep","ttp","tic"};
	
	// TICSS Link Down is different
	public static final int TICSS_LINK_DOWN					= 0;	
}
