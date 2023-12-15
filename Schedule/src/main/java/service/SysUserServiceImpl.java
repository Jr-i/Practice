package service;

import mapper.SysUserMapper;
import pojo.SysUser;
import pojo.SysUserExample;

import java.util.List;

import static util.MD5Util.encrypt;
import static util.MybatisUtil.getSqlSessionFactory;

public class SysUserServiceImpl implements SysUserService {
    private final static SysUserMapper userMapper = getSqlSessionFactory().openSession(true)
            .getMapper(SysUserMapper.class);

    @Override
    public int insert(SysUser sysUser) {
        // 用户名已存在时，返回0
        if (findByUsername(sysUser.getUsername()).size() != 0) {
            return 0;
        }
        // 将用户的明文密码转换为密文密码
        sysUser.setUserPwd(encrypt(sysUser.getUserPwd()));
        // 返回成功写入条数
        return userMapper.insertSelective(sysUser);
    }

    @Override
    public List<SysUser> findByUsername(String username) {
        SysUserExample userExample = new SysUserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        return userMapper.selectByExample(userExample);
    }
}