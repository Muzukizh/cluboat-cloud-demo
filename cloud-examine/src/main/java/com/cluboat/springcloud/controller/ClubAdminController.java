package com.cluboat.springcloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cluboat.springcloud.entities.CommonResult;
import com.cluboat.springcloud.entity.*;
import com.cluboat.springcloud.entity.apply.BudgetApplyEntity;
import com.cluboat.springcloud.mapper.ClubMaMapper;
import com.cluboat.springcloud.service.ClubAdminService;

import com.cluboat.springcloud.service.ClubMaService;
import com.cluboat.springcloud.service.ClubService;
import com.github.yulichang.query.MPJQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/clubadmin")
public class ClubAdminController {


    @Resource
    private ClubAdminService clubAdminService;
    @Resource
    private ClubMaService clubMaService;
    @DeleteMapping
    public CommonResult deleteById(@RequestParam int user_id,@RequestParam int club_id) {
        QueryWrapper<ClubAdminEntity> wrapper=new QueryWrapper<>();

        wrapper.eq("user_id",user_id);
        wrapper.eq("club_id",club_id);

        try {
            clubAdminService.remove(wrapper);
            return new CommonResult(200, "删除成功");
        } catch (Exception e){
            return new CommonResult(400, "删除失败",e);
        }

    }

    @PostMapping
    public CommonResult updateById(@RequestParam int user_id,@RequestParam int club_id,@RequestParam byte permission) {
        QueryWrapper<ClubAdminEntity> wrapper=new QueryWrapper<>();
        //wrapper.eq相当于Map.put,放的是查询条件
        wrapper.eq("user_id",user_id);
        wrapper.eq("club_id",club_id);
        ClubAdminEntity clubAdmin = clubAdminService.getOne(wrapper,false);
        if(clubAdmin==null)
        {
            return new CommonResult(400, "不存在或者未加入社团");

        }
        clubAdmin.setPermission(permission);
        clubAdminService.updateByMultiId(clubAdmin);
        return new CommonResult(200, "修改成功",clubAdmin);

    }
    @Resource
    private ClubMaMapper clubMaMapper;
    @Resource
    private ClubService clubService;
    @GetMapping
    public CommonResult getById() {
        List<ClubAdminEntity> clubAdminList = clubAdminService.list();
        List<ClubMaster> clubMaList = new ArrayList<>();
        try {
            for (int i = 0; i < clubAdminList.size(); i++) {
                System.out.println(clubAdminList.get(i));
                if (clubAdminList.get(i).getPermission() == 1) {
                    ClubMaster clubMaster = new ClubMaster();
                    clubMaster.userId = clubAdminList.get(i).getUserId();
                    clubMaster.clubId = clubAdminList.get(i).getClubId();
                    clubMaList.add(clubMaster);
                }
            }
            for (int i = 0; i < clubMaList.size(); i++) {
                UserInfoEntity s = clubMaMapper.selectById(clubMaList.get(i).userId);
                clubMaList.get(i).userName = s.getUserName();
                clubMaList.get(i).userPhotoUrl = s.getUserPhotoUrl();
            }
            for (int i = 0; i < clubMaList.size(); i++) {
                ClubEntity s = clubService.getById(clubMaList.get(i).clubId);
                clubMaList.get(i).clubName = s.getClubName();
            }
            return new CommonResult(200, "查询成功", clubMaList);
        }catch (Exception e){
            return new CommonResult(400, "查询失败",e);
        }
    }







    }
