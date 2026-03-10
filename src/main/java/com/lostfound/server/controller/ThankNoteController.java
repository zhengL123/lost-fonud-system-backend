package com.lostfound.server.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lostfound.server.util.Result;
import com.lostfound.server.entity.ThankNote;
import com.lostfound.server.service.ThankNoteService;
import com.lostfound.server.util.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 感谢信控制器
 */
@RestController
@RequestMapping("/api/thank-notes")
@RequiredArgsConstructor
public class ThankNoteController {

    private final ThankNoteService thankNoteService;

    /**
     * 创建感谢信
     */
    @PostMapping
    public Result<String> createThankNote(@RequestBody ThankNote thankNote) {
        boolean saved = thankNoteService.save(thankNote);
        return saved ? Result.success("感谢信创建成功") : Result.error("创建失败");
    }

    /**
     * 分页查询感谢信
     */
    @GetMapping("/page")
    public Result<PageResult<ThankNote>> getThankNotePage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer rating) {
        
        Page<ThankNote> page = new Page<>(current, size);
        Page<ThankNote> thankNotePage = thankNoteService.getThankNotePage(page, rating);
        // 转换为自定义分页响应格式
        PageResult<ThankNote> pageResult = PageResult.of(thankNotePage);
        return Result.success(pageResult);
    }

    /**
     * 查询所有感谢信
     */
    @GetMapping("/all")
    public Result<List<ThankNote>> getAllThankNotes() {
        List<ThankNote> thankNotes = thankNoteService.getLatestNotes(100);
        return Result.success(thankNotes);
    }

    /**
     * 查询最新感谢信（带关联信息）
     */
    @GetMapping("/latest")
    public Result<List<ThankNote>> getLatestThankNotes() {
        List<ThankNote> thankNotes = thankNoteService.getLatestNotes(10);
        return Result.success(thankNotes);
    }

    /**
     * 根据ID查询感谢信详情
     */
    @GetMapping("/{id}")
    public Result<ThankNote> getById(@PathVariable Long id) {
        ThankNote thankNote = thankNoteService.getById(id);
        return thankNote != null ? Result.success(thankNote) : Result.error("感谢信不存在");
    }

    /**
     * 更新感谢信
     */
    @PutMapping("/{id}")
    public Result<String> updateById(@PathVariable Long id, @RequestBody ThankNote thankNote) {
        thankNote.setId(id);
        boolean updated = thankNoteService.updateById(thankNote);
        return updated ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除感谢信
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        boolean removed = thankNoteService.removeById(id);
        return removed ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 根据认领记录ID查询感谢信
     */
    @GetMapping("/record/{recordId}")
    public Result<List<ThankNote>> getByRecordId(@PathVariable Long recordId) {
        List<ThankNote> thankNotes = thankNoteService.getThankNotesByRecordId(recordId);
        return Result.success(thankNotes);
    }

    /**
     * 根据评分查询感谢信
     */
    @GetMapping("/rating/{rating}")
    public Result<List<ThankNote>> getByRating(@PathVariable Integer rating) {
        List<ThankNote> thankNotes = thankNoteService.getThankNotesByRating(rating);
        return Result.success(thankNotes);
    }

    /**
     * 获取平均评分
     */
    @GetMapping("/average-rating")
    public Result<Double> getAverageRating() {
        Double averageRating = thankNoteService.getAverageRating();
        return Result.success(averageRating);
    }

    /**
     * 获取高评分感谢信（4分以上）
     */
    @GetMapping("/high-rating")
    public Result<List<ThankNote>> getHighRatingNotes() {
        List<ThankNote> thankNotes = thankNoteService.getThankNotesByRating(4);
        return Result.success(thankNotes);
    }

    /**
     * 根据物品ID获取感谢信
     */
    @GetMapping("/item/{itemId}")
    public Result<List<ThankNote>> getByItemId(@PathVariable Long itemId) {
        List<ThankNote> thankNotes = thankNoteService.getThankNotesByItemId(itemId);
        return Result.success(thankNotes);
    }

    /**
     * 根据用户ID获取感谢信
     */
    @GetMapping("/user/{userId}")
    public Result<List<ThankNote>> getByUserId(@PathVariable Long userId) {
        List<ThankNote> thankNotes = thankNoteService.getThankNotesByUserId(userId);
        return Result.success(thankNotes);
    }
}