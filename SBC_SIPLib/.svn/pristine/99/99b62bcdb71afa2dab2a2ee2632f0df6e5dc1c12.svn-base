package org.linphone.core;


public enum LinphonePrivacyMask {	
		  
	
    LinphonePrivacyNone(0x0),
    /**
     * Request that privacy services provide a user-level privacy
     * function.
     * With this mode, "from" header is hidden, usually replaced by  From: "Anonymous" <sip:anonymous@anonymous.invalid>
     */
    LinphonePrivacyUser(0x1),
    /**
     * Request that privacy services modify headers that cannot
     * be set arbitrarily by the user (Contact/Via).
     */
    LinphonePrivacyHeader(0x2),
    /**
     *  Request that privacy services provide privacy for session
     * media
     */
    LinphonePrivacySession(0x4),
    /**
     * rfc3325
     * The presence of this privacy type in
     * a Privacy header field indicates that the user would like the Network
     * Asserted Identity to be kept private with respect to SIP entities
     * outside the Trust Domain with which the user authenticated.  Note
     * that a user requesting multiple types of privacy MUST include all of
     * the requested privacy types in its Privacy header field value
     *
     */
    LinphonePrivacyId(0x8),
    /**
     * Privacy service must perform the specified services or
     * fail the request
     *
     **/
    LinphonePrivacyCritical(0x10),
    
    /**
     * Special keyword to use privacy as defined either globally or by proxy using linphone_proxy_config_set_privacy()
     */
    LinphonePrivacyDefault(0x8000);
    
    private final int id;
    LinphonePrivacyMask(int id) { this.id = id; }
    public int getValue() { return id; }    
} 



