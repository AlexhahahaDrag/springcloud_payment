package com.alex.springcloud.service.impl;

import com.alex.springcloud.mapper.StorageMapper;
import com.alex.springcloud.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Autowired
    private StorageMapper storageMapper;

    @Override
    public int decrease(Long productId, Integer count) {
        storageMapper.decrease(productId, count);
        return 1;
    }
}
