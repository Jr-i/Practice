package pojo;

public class SysUser {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_user.uid
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    private Integer uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_user.username
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    private String username;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sys_user.user_pwd
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    private String userPwd;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_user.uid
     *
     * @return the value of sys_user.uid
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    public Integer getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_user.uid
     *
     * @param uid the value for sys_user.uid
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_user.username
     *
     * @return the value of sys_user.username
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    public String getUsername() {
        return username;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_user.username
     *
     * @param username the value for sys_user.username
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sys_user.user_pwd
     *
     * @return the value of sys_user.user_pwd
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    public String getUserPwd() {
        return userPwd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sys_user.user_pwd
     *
     * @param userPwd the value for sys_user.user_pwd
     *
     * @mbggenerated Mon Dec 11 15:22:01 CST 2023
     */
    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd == null ? null : userPwd.trim();
    }
}