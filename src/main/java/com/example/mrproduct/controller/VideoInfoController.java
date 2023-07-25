package com.example.mrproduct.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import com.example.mrproduct.service.IVideoInfoService;
import com.example.mrproduct.entity.VideoInfo;

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
@RequestMapping("//video-info")
public class VideoInfoController {

    @Resource
    private IVideoInfoService videoInfoService;

    // 新增或者更新
    @PostMapping
    public boolean save(@RequestBody VideoInfo videoInfo) {
        return videoInfoService.saveOrUpdate(videoInfo);
    }

    // 删除
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return videoInfoService.removeById(id);
    }

    // 查询所有数据
    @GetMapping
    public List<VideoInfo> findAll() {
        return videoInfoService.list();
    }

    // 根据id查询
    @GetMapping("/{id}")
    public VideoInfo findOne(@PathVariable Integer id) {
        return videoInfoService.getById(id);
    }

    // 分页查询
    @GetMapping("/page")
    public Page<VideoInfo> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return videoInfoService.page(new Page<>(pageNum, pageSize));
    }

}

