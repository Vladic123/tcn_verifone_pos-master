package com.tcn.sdk.liftdemo;

import com.tcn.liftboard.control.GroupInfo;
import com.tcn.liftboard.control.TcnConstantLift;
import com.tcn.liftboard.control.TcnVendIF;

import java.util.ArrayList;
import java.util.List;

public class UIComBack {
	private static final String TAG = "UIComBack";
	public static String[] HEAT_COOL_OFF_SWITCH_SELECT = {"制冷","加热","关闭"};
	private static UIComBack m_Instance = null;

	private List<GropInfoBack> m_GrpShowListAll = new ArrayList<GropInfoBack>();
	private List<GropInfoBack> m_GrpShowListElevator = new ArrayList<GropInfoBack>();


	public static synchronized UIComBack getInstance() {
		if (null == m_Instance) {
			m_Instance = new UIComBack();
		}
		return m_Instance;
	}

	public int getGroupElevatorId(String data) {
		int iId = -1;
		if ((null == data) || (data.length() < 1)) {
			return iId;
		}

		for (GropInfoBack info:m_GrpShowListElevator) {
			if (data.equals(info.getShowText())) {
				iId = info.getGrpID();
			}
		}
		return iId;
	}

	public boolean isMutiGrpElevator() {
		boolean bRet = false;
		List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListElevator();
		if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
			bRet = true;
		}
		return bRet;
	}

	public List<GropInfoBack> getGroupListAll() {
		m_GrpShowListAll.clear();
		List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListAll();
		if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
			for (int i = 0; i < mGroupInfoList.size(); i++) {
				GroupInfo info = mGroupInfoList.get(i);
				GropInfoBack mGropInfoBack = new GropInfoBack();
				mGropInfoBack.setID(i);
				mGropInfoBack.setGrpID(info.getID());
				if (info.getID() == 0) {
					mGropInfoBack.setShowText("主柜");
				} else {
					mGropInfoBack.setShowText("副柜"+info.getID());
				}
				m_GrpShowListAll.add(mGropInfoBack);
			}
		}
		return m_GrpShowListAll;
	}

	public String[] getGroupListElevatorShow() {
		List<String> m_RetList = new ArrayList<String>();
		if (m_GrpShowListElevator.size() < 1) {
			List<GroupInfo> mGroupInfoList = TcnVendIF.getInstance().getGroupListElevator();
			if ((mGroupInfoList != null) && (mGroupInfoList.size() > 1)) {
				for (int i = 0; i < mGroupInfoList.size(); i++) {
					GroupInfo info = mGroupInfoList.get(i);
					GropInfoBack mGropInfoBack = new GropInfoBack();
					mGropInfoBack.setID(i);
					mGropInfoBack.setGrpID(info.getID());
					if (info.getID() == 0) {
						mGropInfoBack.setShowText("主柜");
					} else {
						mGropInfoBack.setShowText("副柜"+info.getID());
					}
					m_GrpShowListElevator.add(mGropInfoBack);
				}
			}
		}

		for (GropInfoBack info:m_GrpShowListElevator) {
			m_RetList.add(info.getShowText());
		}
		if (m_RetList.size() < 1) {
			return null;
		}
		String[] strArry = new String[m_RetList.size()];
		for (int i = 0; i < m_RetList.size(); i++) {
			strArry[i] = m_RetList.get(i);
		}
		return strArry;
	}

	public int getQueryParameters(String data) {
		int iAddress = -1;
		if ((null == data) || (data.length() < 1)) {
			return iAddress;
		}

		for (int i = 0; i < (TcnConstantLift.LIFT_QUERY_PARAM).length; i++) {
			if ((TcnConstantLift.LIFT_QUERY_PARAM[i]).equals(data)) {
				iAddress = i;
				break;
			}
		}

		return iAddress;
	}
}
