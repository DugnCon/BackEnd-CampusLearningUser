package com.javaweb.service.impl.EventService;

import com.javaweb.entity.Event.EventEntity;
import com.javaweb.entity.Event.EventParticipantsEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.IEventParticipantsRepository;
import com.javaweb.repository.IEventRepository;
import com.javaweb.repository.IUserRepository;
import com.javaweb.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

//import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Service
public class EventServiceImpl implements IEventService {
    @Autowired
    private IEventRepository eventRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IEventParticipantsRepository eventParticipantsRepository;
    @Override
    public ResponseEntity<Object> getAllEvent() {
        try {
            List<EventEntity> eventEntities = eventRepository.findAll();
            if(eventEntities.isEmpty()) {
                return ResponseEntity.ok(Map.of("success", true, "message", "Không có sự kiện nào diễn ra", "event", eventEntities));
            }
            return ResponseEntity.ok(eventEntities);
        } catch (Exception e) {
            throw new RuntimeException(e + " error by getting event list");
        }
    }
    @Override
    public ResponseEntity<Object> getEventPreview(Long eventId) {
        try {
            EventEntity eventEntity = eventRepository.getEventPreview(eventId);
            return ResponseEntity.ok(eventEntity);
        } catch (Exception e) {
            throw new RuntimeException(e +  " error by getting event preview");
        }
    }
    @Override //Status IN ('attended', 'cancelled', 'confirmed', 'registered')
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public ResponseEntity<Object> eventRegister(Long eventId, Long userId, Map<String,Object> userData) {
        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("not found event"));
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found user"));

        try {
            EventParticipantsEntity eventParticipantsEntityExists = eventParticipantsRepository.getSingleEventParticipant(eventId, userId);

            if(eventParticipantsEntityExists != null && eventParticipantsEntityExists.getStatus().equals("cancelled")) {
                eventParticipantsEntityExists.setStatus("registered");
                eventParticipantsRepository.save(eventParticipantsEntityExists);

                Map<String,Object> registationInfo = Map.of("EventID", eventId, "UserID", userId, "TeamName", eventParticipantsEntityExists.getTeamName());


                return ResponseEntity.ok(Map.of("message", "Đã đăng kí", "registationInfo", registationInfo));
            }

            if(eventEntity.getPrice().compareTo(BigDecimal.ZERO) > 0) {

                EventParticipantsEntity eventParticipantsEntity = new EventParticipantsEntity();
                eventParticipantsEntity.setStatus("registered");
                eventParticipantsEntity.setEvent(eventEntity);
                eventParticipantsEntity.setUser(userEntity);
                eventParticipantsEntity.setPaymentStatus("pending");
                eventParticipantsEntity.setAttendanceStatus("pending");
                eventParticipantsEntity.setTeamName(userEntity.getUsername());

                eventParticipantsRepository.save(eventParticipantsEntity);

                Map<String,Object> registationInfo = Map.of("EventID", eventId, "UserID", userId, "TeamName", eventParticipantsEntity.getTeamName());

                return ResponseEntity.ok(Map.of("message", "Đã đăng kí", "registationInfo", registationInfo));

            } else {

                EventParticipantsEntity eventParticipantsEntity = new EventParticipantsEntity();
                eventParticipantsEntity.setStatus("registered");
                eventParticipantsEntity.setEvent(eventEntity);
                eventParticipantsEntity.setUser(userEntity);
                eventParticipantsEntity.setPaymentStatus("free");
                eventParticipantsEntity.setAttendanceStatus("pending");
                eventParticipantsEntity.setTeamName(userEntity.getUsername());

                eventParticipantsRepository.save(eventParticipantsEntity);

                Map<String,Object> registationInfo = Map.of("EventID", eventId, "UserID", userId, "TeamName", eventParticipantsEntity.getTeamName());

                return ResponseEntity.ok(Map.of("message", "Đã đăng kí", "registationInfo", registationInfo));

            }
        } catch (Exception e) {
            throw new RuntimeException(e + " can not regist event");
        }
    }
    @Override
    public ResponseEntity<Object> eventRegistrationStatus(Long eventId, Long userId) {
        try {
            EventParticipantsEntity eventParticipantsEntity = eventParticipantsRepository.getSingleEventParticipant(eventId, userId);
            if(eventParticipantsEntity.getStatus().equals("registered")) {
                Map<String,Object> registationInfo = Map.of("EventID", eventId, "UserID", userId, "TeamName", eventParticipantsEntity.getTeamName());
                return ResponseEntity.ok(Map.of("isRegistered", true, "registationInfo", registationInfo));
            } else {
                return ResponseEntity.ok(Map.of("isRegistered", false, "registationInfo", null));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = Exception.class)
    public ResponseEntity<Object> eventCancelRegistration(Long eventId, Long userId) {
        //EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("not found event"));
        //UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("not found user"));
        try {
          EventParticipantsEntity eventParticipantsEntity = eventParticipantsRepository.getSingleEventParticipant(eventId, userId);
          eventParticipantsEntity.setStatus("cancelled");
          eventParticipantsRepository.save(eventParticipantsEntity);
            Map<String,Object> registationInfo = Map.of("EventID", eventId, "UserID", userId, "TeamName", eventParticipantsEntity.getTeamName());
          return ResponseEntity.ok(Map.of("Status", "Cancelled", "registationInfo", registationInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
