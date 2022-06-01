package com.project.fastfoodapi.listener;


import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;

@Component
@RequiredArgsConstructor
public class HibernateEventRegister {

    private final EntityManagerFactory entityManagerFactory;
    private final HibernateEventListener hibernateInsertListener;

    @PostConstruct
    public void registerListeners() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).prependListener(hibernateInsertListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).prependListener(hibernateInsertListener);
    }
}