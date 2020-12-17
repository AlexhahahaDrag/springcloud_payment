package com.alex.springcloud.service;

import com.alex.springcloud.entity.SysDict;
import com.alex.springcloud.mapper.SysDictMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
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

    public SysDict findSysDict(String sysCode, String code, String belongTo) {
        QueryWrapper<SysDict> wrapper = new QueryWrapper<>();
        if (StringUtils.isBlank(sysCode))
            wrapper.isNull("sys_code");
        else
            wrapper.eq("sys_code", sysCode);
        if (StringUtils.isBlank(code))
            wrapper.isNull("code");
        else
            wrapper.eq("code", code);
        if (StringUtils.isBlank(belongTo)) {
            wrapper.isNull("belong_to");
        } else
            wrapper.eq("belong_to", belongTo);
        return sysDictMapper.selectOne(wrapper);
    }
}
