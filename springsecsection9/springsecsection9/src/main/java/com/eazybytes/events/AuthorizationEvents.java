package com.eazybytes.events;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class AuthorizationEvents {


    @EventListener
    public void onFailure(AuthorizationDeniedEvent failureEvent){
        log.error("Authentication Fail for the user : {} due to : {}", failureEvent.getAuthentication().get().getName(),
                failureEvent.getAuthorizationDecision().toString());
    }


}
