package com.project.fastfoodapi.listener;

import com.project.fastfoodapi.entity.Order;
import com.project.fastfoodapi.entity.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HibernateEventListener implements PostInsertEventListener, PostUpdateEventListener {
    final SimpMessageSendingOperations messageSendingOperations;

    @Override
    public void onPostInsert(PostInsertEvent postInsertEvent) {
        Object entity = postInsertEvent.getEntity();
        if (entity instanceof Order order) {
            Map<String, Object> res = new LinkedHashMap<>();
            res.put("status", order.getOrderStatus());
            res.put("type", "NEW");
            messageSendingOperations.convertAndSend("/update/order", res);
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
        Object entity = postUpdateEvent.getEntity();
        if (entity instanceof Order order) {
            Map<String, Object> res = new LinkedHashMap<>();
            for (Object obj : postUpdateEvent.getOldState()) {
                if (obj instanceof OrderStatus orderStatus) {
                    res.put("oldStatus", orderStatus);
                    break;
                }
            }
            res.put("status", order.getOrderStatus());
            res.put("type", "UPDATE");
            messageSendingOperations.convertAndSend("/update/order", res);
        }
    }
}