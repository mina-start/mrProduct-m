package com.example.mrproduct.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.mrproduct.service.IOrderSaveUsedService;
import com.example.mrproduct.entity.OrderSaveUsed;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mina
 * @since 2023-07-19
 */
@RestController
@RequestMapping("//order-save-used")
public class OrderSaveUsedController {

    @Resource
    private IOrderSaveUsedService orderSaveUsedService;

    // 新增或者更新
    @PostMapping
    public boolean save(@RequestBody OrderSaveUsed orderSaveUsed) {
        return orderSaveUsedService.saveOrUpdate(orderSaveUsed);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return orderSaveUsedService.removeById(id);
    }

    // 查询所有数据
    @GetMapping
    public List<OrderSaveUsed> findAll() {
        return orderSaveUsedService.list();
    }

    // 根据id查询
    @GetMapping("/{id}")
    public OrderSaveUsed findOne(@PathVariable Integer id) {
        return orderSaveUsedService.getById(id);
    }

    // 分页查询
    @GetMapping("/page")
    public Page<OrderSaveUsed> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return orderSaveUsedService.page(new Page<>(pageNum, pageSize));
    }

}

