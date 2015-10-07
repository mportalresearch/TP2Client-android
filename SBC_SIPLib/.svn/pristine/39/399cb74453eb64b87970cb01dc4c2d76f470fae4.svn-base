package org.linphone.service;

import java.util.ArrayList;



class session {
	String sessionType;
	Integer sessionId;
	String joinStr;
	String state; 
}

class Add {
	String handleId;
	ArrayList<session> sessionDetails;
}

class participants {
	Integer id; 
	String media;
}

class Modify {
	ArrayList<participants> participantsDetails;
	public String handleId;
}

public class Publish {
	 
	 ArrayList<Add> mAddSessionDetails;
	 ArrayList<Modify> mModifySessionDetails;
	 Publish(ArrayList<Add> AddSessionDetails,ArrayList<Modify> ModifySessionDetails) {
		 //mAddSessionDetails = new ArrayList<Add>();
		 //mModifySessionDetails = new ArrayList<Modify>(); 
		 mAddSessionDetails = AddSessionDetails;
		 mModifySessionDetails = ModifySessionDetails;
	 }
}
