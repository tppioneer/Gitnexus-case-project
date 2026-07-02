package com.example.telecom.common.user;

/**
 * Operator user with their own regionCode.
 * This is a DISTRACTION field for Case C — NOT to be modified when DeviceInfo.regionCode is renamed.
 */
public class OperatorUser {
    private String userId;
    private String userName;
    private String regionCode;
    private String phone;
    private String email;
    private String skillGroup;

    public OperatorUser() {}

    public OperatorUser(String userId, String userName, String regionCode, String phone,
                        String email, String skillGroup) {
        this.userId = userId;
        this.userName = userName;
        this.regionCode = regionCode;
        this.phone = phone;
        this.email = email;
        this.skillGroup = skillGroup;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSkillGroup() { return skillGroup; }
    public void setSkillGroup(String skillGroup) { this.skillGroup = skillGroup; }
}
