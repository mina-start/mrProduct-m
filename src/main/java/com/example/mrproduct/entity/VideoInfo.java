package com.example.mrproduct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 *
 * </p>
 *
 * @author mina
 * @since 2023-07-19
 */
@Getter
@Setter
@TableName("video_info")
@ApiModel(value = "VideoInfo对象", description = "")
public class VideoInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String blindUserName;

    private String serverIp;

    private Integer cameraNum;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;


}
