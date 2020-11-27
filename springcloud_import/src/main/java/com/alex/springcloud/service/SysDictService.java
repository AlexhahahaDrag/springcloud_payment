package com.alex.springcloud.service;

import com.alex.springcloud.entity.SysDict;
import com.alex.springcloud.mapper.SysDictMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author:      alex
 * @createTime:  2020/11/27 11:42
 * @version:     1.0
 */
@Service
public class SysDictService {

    @Autowired
    private SysDictMapper sysDictMapper;

    public SysDict findSysDict(String sysCode, String code) {
        QueryWrapper<SysDict> wrapper = new QueryWrapper<>();
        wrapper.eq("sys_code", sysCode);
        wrapper.eq("code", code);
        return sysDictMapper.selectOne(wrapper);
    }
}
