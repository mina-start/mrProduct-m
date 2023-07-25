package com.example.mrproduct.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import com.example.mrproduct.service.IBlindAppUserService;
import com.example.mrproduct.entity.BlindAppUser;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mina
 * @since 2023-07-19
 */
@RestController
@RequestMapping("/blind-app-user")
public class BlindAppUserController {
    
        @Resource
        private IBlindAppUserService blindAppUserService;

        // 新增或者更新
        @PostMapping
        public boolean save(@RequestBody BlindAppUser blindAppUser) {
                return blindAppUserService.saveOrUpdate(blindAppUser);
        }

        // 删除
        @DeleteMapping("/{id}")
        public boolean delete(@PathVariable Integer id) {
                return blindAppUserService.removeById(id);
        }

        // 查询所有数据
        @GetMapping
        public List<BlindAppUser> findAll() {
                return blindAppUserService.list();
        }

        // 根据id查询
        @GetMapping("/{id}")
        public BlindAppUser findOne(@PathVariable Integer id) {
                return blindAppUserService.getById(id);
        }

        // 分页查询
        @GetMapping("/page")
        public Page<BlindAppUser> findPage(@RequestParam Integer pageNum,@RequestParam Integer pageSize) {
                return blindAppUserService.page(new Page<>(pageNum, pageSize));
        }

}

