package service;

import mapper.SysUserMapper;
import pojo.SysUser;
import util.MybatisUtil;

import static util.MD5Util.encrypt;

public class SysUserServiceImpl implements SysUserService {
    @Override
    public int regist(SysUser registUser) {
        // 将用户的明文密码转换为密文密码
        registUser.setUserPwd(encrypt(registUser.getUserPwd()));
        // 调用DAO 层的方法  将sysUser信息存入数据库
        SysUserMapper userMapper = MybatisUtil.getSqlSessionFactory().openSession(true)
                .getMapper(SysUserMapper.class);
        // 返回成功写入条数，为0则表示写入失败
        return userMapper.insert(registUser);
    }
}