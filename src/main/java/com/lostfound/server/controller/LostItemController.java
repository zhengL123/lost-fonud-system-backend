package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.dto.LostItemDTO;
import com.lostfound.server.entity.LostItem;
import com.lostfound.server.entity.User;
import com.lostfound.server.service.LostItemService;
import com.lostfound.server.service.UserService;
import com.lostfound.server.util.LostItemServiceHelper;
import com.lostfound.server.util.PageResult;
import com.lostfound.server.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 失物招领控制器
 * 提供失物信息的增删改查功能，包括：
 * 1. 新增失物信息（支持图片上传）
 * 2. 查询失物详情（按ID、按状态、分页查询、全量查询）
 * 3. 修改失物信息
 * 4. 删除失物信息
 * 
 * @author 系统开发团队
 * @version 1.0
 */
@RestController
@RequestMapping("/api/lost-items")
@RequiredArgsConstructor
public class LostItemController {

    // 注入失物服务层，用于处理业务逻辑
    private final LostItemService lostItemService;
    // 注入失物服务辅助工具类，用于参数验证和响应创建
    private final LostItemServiceHelper lostItemServiceHelper;
    // 注入用户服务层，用于获取发布者信息
    private final UserService userService;

    /**
     * 新增失物信息（支持图片上传）
     * 
     * 接收前端传递的失物信息DTO，验证后保存到数据库
     * 支持多张图片上传，图片URL列表存储在itemImages字段中
     * 
     * @param lostItemDTO 失物信息DTO对象，包含物品名称、描述、丢失地点和图片URL列表等信息
     * @return Result<LostItem> 包含操作结果和保存后的失物信息
     */
    @PostMapping
    public Result<LostItem> createLostItem(@RequestBody LostItemDTO lostItemDTO) {
        // 将DTO转换为实体对象
        LostItem lostItem = new LostItem();
        BeanUtils.copyProperties(lostItemDTO, lostItem);
        
        // 手动设置可能无法正确复制的字段
        lostItem.setCreatorId(lostItemDTO.getCreatorId());
        // 修复字段名不匹配问题：DTO中的itemDescription映射到实体中的description
        // 优先使用itemDescription字段，如果为空则使用description字段
        if (lostItemDTO.getItemDescription() != null && !lostItemDTO.getItemDescription().isEmpty()) {
            lostItem.setDescription(lostItemDTO.getItemDescription());
        } else if (lostItemDTO.getDescription() != null && !lostItemDTO.getDescription().isEmpty()) {
            lostItem.setDescription(lostItemDTO.getDescription());
        }
        // 修复字段类型不匹配问题：DTO中的Integer status映射到实体中的String status
        if (lostItemDTO.getStatus() != null) {
            // 将Integer状态转换为String状态
            switch (lostItemDTO.getStatus()) {
                case 0:
                    lostItem.setStatus("LOST");
                    break;
                case 1:
                    lostItem.setStatus("FOUND");
                    break;
                case 2:
                    lostItem.setStatus("RETURNED");
                    break;
                default:
                    lostItem.setStatus("LOST");
                    break;
            }
        } else {
            // 默认状态为LOST
            lostItem.setStatus("LOST");
        }
        if (lostItemDTO.getLostTime() != null) {
            lostItem.setLostTime(lostItemDTO.getLostTime().toString());
        }
        
        // 设置图片列表
        lostItem.setImageList(lostItemDTO.getItemImages());
        
        // 设置创建时间和更新时间
        String currentTime = java.time.LocalDateTime.now().toString();
        lostItem.setCreatedTime(currentTime);
        lostItem.setUpdateTime(currentTime);
        
        // 使用辅助工具类验证失物信息的完整性和有效性
        Result validationResult = lostItemServiceHelper.validateLostItem(lostItem);
        if (validationResult.getCode() != 200) {
            // 验证失败，返回错误信息
            return Result.error(validationResult.getMessage());
        }
        
        // 保存失物信息到数据库
        boolean saved = lostItemService.save(lostItem);
        if (saved) {
            // 保存成功，获取数据库中生成的完整失物信息（包括ID等自动生成字段）
            LostItem savedItem = lostItemService.getById(lostItem.getId());
            return Result.success(savedItem);
        } else {
            // 保存失败，返回错误信息
            return Result.error("创建失败");
        }
    }

    /**
     * 根据ID查询失物详情
     * 
     * 通过失物ID获取完整的失物信息，包括物品描述、丢失地点、联系方式等
     * 
     * @param id 失物信息的唯一标识符
     * @return Result<LostItem> 包含失物详情信息，如果不存在则返回错误信息
     */
    @GetMapping("/{id}")
    public Result<LostItem> getById(@PathVariable Long id) {
        // 根据ID从数据库查询失物信息
        LostItem lostItem = lostItemService.getById(id);
        // 如果查询到结果，返回成功响应；否则返回错误响应
        if (lostItem != null) {
            // 获取发布者信息
            if (lostItem.getCreatorId() != null) {
                User user = userService.getById(lostItem.getCreatorId());
                if (user != null) {
                    lostItem.setCreatorName(user.getUsername());
                }
            }
            // 确保图片数据正确格式化
            // 将itemImages字段设置为解析后的JSON字符串，确保前端可以正确处理
            return Result.success(lostItem);
        } else {
            return Result.error("失物信息不存在");
        }
    }

    /**
     * 分页查询失物列表
     * 
     * 支持按物品名称、丢失地点和状态进行条件筛选的分页查询
     * 默认按创建时间降序排序，最新发布的失物信息排在前面
     * 
     * @param pageNum 页码，从1开始，默认为1
     * @param pageSize 每页记录数，默认为10
     * @param itemName 物品名称，支持模糊查询（可选）
     * @param lostLocation 丢失地点，支持模糊查询（可选）
     * @param status 失物状态（LOST/FOUND/RETURNED），精确匹配（可选）
     * @return Result<PageResult<LostItem>> 包含分页数据和分页信息的自定义分页对象
     */
    @GetMapping("/page")
    public Result<PageResult<LostItem>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String lostLocation,
            @RequestParam(required = false) String status) {

        // 验证分页参数的有效性（页码必须大于0，每页数量在1-100之间）
        Result validationResult = lostItemServiceHelper.validatePageParams(pageNum, pageSize);
        if (validationResult.getCode() != 200) {
            // 分页参数无效，返回400错误请求
            return Result.error(validationResult.getMessage());
        }

        // 如果没有指定状态，默认只显示"丢失中"状态的失物，过滤掉"已找到"和"已归还"
        if (status == null || status.trim().isEmpty()) {
            status = "LOST";
        }

        // 创建分页对象
        Page<LostItem> page = new Page<>(pageNum, pageSize);
        // 调用服务层执行分页查询，支持条件筛选
        Page<LostItem> result = lostItemService.page(page, itemName, lostLocation, status);
        // 转换为自定义分页响应格式
        PageResult<LostItem> pageResult = PageResult.of(result);
        // 返回查询结果
        return Result.success(pageResult);
    }

    /**
     * 查询所有失物（不分页，用于测试）
     * 
     * 获取数据库中所有的失物信息，不进行分页处理
     * 注意：此方法仅用于测试环境，生产环境中数据量大时可能影响性能
     * 
     * @return Result<List<LostItem>> 包含所有失物信息的列表
     */
    @GetMapping("/all")
    public Result<List<LostItem>> getAll() {
        // 查询所有失物信息，不分页
        List<LostItem> list = lostItemService.list();
        // 返回查询结果
        return Result.success(list);
    }

    /**
     * 根据ID修改失物信息
     * 
     * 更新指定ID的失物信息，支持部分字段更新
     * 更新前会验证失物信息的完整性和有效性
     * 
     * @param id 要更新的失物ID
     * @param lostItem 包含更新信息的失物对象
     * @return Result<LostItem> 包含操作结果和更新后的失物信息
     */
    @PutMapping("/{id}")
    public Result<LostItem> updateById(@PathVariable Long id, @RequestBody LostItem lostItem) {
        // 验证失物信息的完整性和有效性
        Result validationResult = lostItemServiceHelper.validateLostItem(lostItem);
        if (validationResult.getCode() != 200) {
            // 验证失败，返回错误信息
            return Result.error(validationResult.getMessage());
        }
        
        // 设置要更新的失物ID
        lostItem.setId(id);
        // 执行更新操作
        boolean updated = lostItemService.updateById(lostItem);
        if (updated) {
            // 更新成功，获取更新后的完整失物信息
            LostItem updatedItem = lostItemService.getById(id);
            return Result.success(updatedItem);
        } else {
            // 更新失败，返回错误信息
            return Result.error("更新失败");
        }
    }

    /**
     * 根据ID删除失物信息
     * 
     * 删除指定ID的失物信息，此操作不可恢复
     * 注意：删除前应确认该失物信息是否有关联的认领记录
     * 
     * @param id 要删除的失物ID
     * @return Result<String> 包含操作结果信息
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        // 执行删除操作
        boolean removed = lostItemService.removeById(id);
        if (removed) {
            // 删除成功，返回成功信息
            return Result.success("删除成功");
        } else {
            // 删除失败，返回错误信息（可能是ID不存在）
            return Result.error("删除失败");
        }
    }

    /**
     * 根据状态查询失物列表
     * 
     * 获取指定状态的所有失物信息，支持以下状态：
     * - LOST: 丢失中
     * - FOUND: 已找到
     * - RETURNED: 已归还
     * 
     * @param status 失物状态（LOST/FOUND/RETURNED）
     * @return Result<List<LostItem>> 包含指定状态的失物信息列表
     */
    @GetMapping("/status/{status}")
    public Result<List<LostItem>> getByStatus(@PathVariable String status) {
        // 根据状态查询失物信息
        List<LostItem> items = lostItemService.getItemsByStatus(status);
        // 返回查询结果
        return Result.success(items);
    }

    /**
     * 获取最新失物信息
     * 
     * 获取最新发布的几条失物信息，按创建时间降序排序
     * 默认返回最新的5条记录
     * 
     * @return Result<List<LostItem>> 包含最新失物信息的列表
     */
    @GetMapping("/latest")
    public Result<List<LostItem>> getLatest() {
        // 查询所有失物信息，按创建时间降序排序
        List<LostItem> allItems = lostItemService.list();
        
        // 如果列表不为空，返回前5条记录；否则返回空列表
        List<LostItem> latestItems = allItems.size() > 5 ? 
            allItems.subList(0, 5) : allItems;
            
        // 返回查询结果
        return Result.success(latestItems);
    }

    /**
     * 调试接口 - 检查图片数据
     * 
     * 临时添加的调试接口，用于检查数据库中的图片数据格式
     * 
     * @param id 失物ID
     * @return Result<String> 包含图片数据的原始字符串
     */
    @GetMapping("/debug/images/{id}")
    public Result<String> debugImages(@PathVariable Long id) {
        LostItem lostItem = lostItemService.getById(id);
        if (lostItem == null) {
            return Result.error("失物信息不存在");
        }
        
        // 返回原始图片数据
        return Result.success(lostItem.getItemImages());
    }
}