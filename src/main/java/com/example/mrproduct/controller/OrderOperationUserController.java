package com.example.mrproduct.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.mrproduct.service.IOrderOperationUserService;
import com.example.mrproduct.entity.OrderOperationUser;

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
@RequestMapping("//order-operation-user")
public class OrderOperationUserController {

    @Resource
    private IOrderOperationUserService orderOperationUserService;

    // 新增或者更新
    @PostMapping
    public boolean save(@RequestBody OrderOperationUser orderOperationUser) {
        return orderOperationUserService.saveOrUpdate(orderOperationUser);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return orderOperationUserService.removeById(id);
    }

    // 查询所有数据
    @GetMapping
    public List<OrderOperationUser> findAll() {
        return orderOperationUserService.list();
    }

    // 根据id查询
    @GetMapping("/{id}")
    public OrderOperationUser findOne(@PathVariable Integer id) {
        return orderOperationUserService.getById(id);
    }

    // 分页查询
    @GetMapping("/page")
    public Page<OrderOperationUser> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return orderOperationUserService.page(new Page<>(pageNum, pageSize));
    }

}

