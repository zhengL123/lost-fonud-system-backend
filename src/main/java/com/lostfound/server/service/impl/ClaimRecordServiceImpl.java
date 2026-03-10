package com.lostfound.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lostfound.server.entity.ClaimRecord;
import com.lostfound.server.entity.Message;
import com.lostfound.server.mapper.ClaimRecordMapper;
import com.lostfound.server.service.ClaimRecordService;
import com.lostfound.server.service.LostItemService;
import com.lostfound.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClaimRecordServiceImpl extends ServiceImpl<ClaimRecordMapper, ClaimRecord> implements ClaimRecordService {

    @Autowired
    private LostItemService lostItemService;
    
    @Autowired
    private MessageService messageService;

    @Override
    public List<ClaimRecord> getRecordsByItemId(Long itemId) {
        return baseMapper.selectByItemId(itemId);
    }

    @Override
    public List<ClaimRecord> getRecordsByUserId(Long claimUserId) {
        return baseMapper.selectByClaimUserId(claimUserId);
    }
    
    @Override
    public List<ClaimRecord> getRecordsByCreatorId(Long creatorId) {
        return baseMapper.selectByCreatorId(creatorId);
    }

    @Override
    public List<ClaimRecord> getRecordsByStatus(String status) {
        return baseMapper.selectByStatus(status);
    }
    
    @Override
    public List<ClaimRecord> getRecordsByStatusWithDetails(String status) {
        return baseMapper.selectByStatus(status);
    }
    
    @Override
    public List<ClaimRecord> getAllRecordsWithDetails() {
        return baseMapper.selectAllWithDetails();
    }

    @Override
    public boolean processClaim(Long recordId, String status, String adminRemark) {
        // 先获取认领记录，以便获取物品ID和用户信息
        ClaimRecord claimRecord = baseMapper.selectById(recordId);
        if (claimRecord == null) {
            return false;
        }
        
        // 获取完整的认领记录，包括物品名称等详细信息
        ClaimRecord recordWithDetails = baseMapper.selectByIdWithDetails(recordId);
        
        // 更新认领记录状态
        int result = baseMapper.updateRecordStatus(recordId, status, adminRemark);
        if (result <= 0) {
            return false;
        }
        
        // 根据认领状态更新物品状态
        if (StringUtils.hasText(status)) {
            String itemStatus = null;
            
            switch (status.trim()) {
                case "APPROVED":
                    // 认领被批准，物品状态更新为"已归还"
                    itemStatus = "RETURNED";
                    break;
                case "COMPLETED":
                    // 认领已完成，物品状态更新为"已归还"
                    itemStatus = "RETURNED";
                    break;
                case "REJECTED":
                    // 认领被拒绝，物品状态保持不变（仍为"丢失中"）
                    // 不需要更新物品状态
                    break;
                case "VERIFYING":
                    // 身份核实中，物品状态保持不变
                    // 不需要更新物品状态
                    break;
                default:
                    // 其他状态不更新物品状态
                    break;
            }
            
            // 如果需要更新物品状态
            if (itemStatus != null) {
                lostItemService.updateItemStatus(claimRecord.getItemId(), itemStatus);
            }
        }
        
        // 发送消息通知用户
        sendNotification(claimRecord, recordWithDetails, status, adminRemark);
        
        return true;
    }
    
    /**
     * 发送审核结果通知消息
     */
    private void sendNotification(ClaimRecord claimRecord, ClaimRecord recordWithDetails, String status, String adminRemark) {
        // 获取物品名称
        String itemName = recordWithDetails != null ? recordWithDetails.getItemName() : "物品";
        
        // 创建消息对象
        Message message = new Message();
        message.setSenderId(1L); // 系统管理员ID
        message.setSenderName("系统管理员");
        message.setReceiverId(claimRecord.getClaimUserId());
        message.setMsgType("SYSTEM");
        message.setIsRead(0); // 0未读
        message.setCreatedTime(LocalDateTime.now());
        
        // 根据审核状态设置消息内容
        if ("APPROVED".equals(status.trim())) {
            message.setTitle("认领申请已通过");
            message.setContent(String.format("您申请认领的物品「%s」已审核通过，请前往景区游客服务中心领取。如有疑问请联系工作人员。", itemName));
        } else if ("REJECTED".equals(status.trim())) {
            message.setTitle("认领申请未通过");
            String reason = StringUtils.hasText(adminRemark) ? adminRemark : "审核未通过";
            message.setContent(String.format("您申请认领的物品「%s」未通过审核。拒绝理由：%s。如有疑问请联系工作人员。", itemName, reason));
        }
        
        // 保存消息
        if (message.getTitle() != null && message.getContent() != null) {
            messageService.save(message);
        }
    }
    
    @Override
    public ClaimRecord getRecordByIdWithDetails(Long id) {
        return baseMapper.selectByIdWithDetails(id);
    }
}