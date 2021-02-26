package com.alex.springcloud.service;

public interface StorageService {

    /**
     * @param productId
     * @param count
     * @description:
     * @author: alex
     * @return: int
     */
    int decrease(Long productId, Integer count);
}
