package com.won.myongjiCamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Key;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@Repository
public class SseRepository  {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter sseEmitter){
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    public void saveEventCache(String eventCacheId, Object event){
        eventCache.put(eventCacheId, event);
    }

    public Map<String, SseEmitter> findAllEmitterStartsWithMemberId(String memberId){
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId){
        return eventCache.entrySet().stream()
                .filter(entry->entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    public void deleteById(String emitterId) {
        emitters.remove(emitterId);

    }

    public void deleteAllEmitterStartsWithId(String memberId){
        emitters.forEach(
                (key, emitter) -> {
                    if(key.startsWith(memberId)){
                        emitters.remove(key);
                    }
                }

        );
    }

    public void deleteAllEventCacheStartsWithId(String memberId){
        eventCache.forEach(
                (key, emitter) -> {
                    if(key.startsWith(memberId)){
                        eventCache.remove(key);
                    }
                }


        );
    }

}
