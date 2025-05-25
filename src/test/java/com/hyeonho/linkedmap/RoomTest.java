//package com.hyeonho.linkedmap;
//
//import com.hyeonho.linkedmap.member.Member;
//import com.hyeonho.linkedmap.roommember.RoomMember;
//import com.hyeonho.linkedmap.invite.InviteState;
//import com.hyeonho.linkedmap.room.repository.RoomRepository;
//import com.hyeonho.linkedmap.roommember.RoomMemberRepository;
//import com.hyeonho.linkedmap.marker.MarkerRepository;
//import com.hyeonho.linkedmap.member.MemberRepository;
//import jakarta.persistence.EntityManager;
//import org.junit.jupiter.api.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//
//@SpringBootTest
//public class RoomTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private RoomRepository roomRepository;
//
//    @Autowired
//    private RoomMemberRepository roomMemberRepository;
//
//    @Autowired
//    private MarkerRepository markerRepository;
//    private static final Logger log = LoggerFactory.getLogger(RoomTest.class);
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Test
//    public void 패치조인없이_특정방에속한_유저리스트_조회() {
//        Long roomId = 2L;
//
//        List<RoomMember> memberList = roomMemberRepository.getRoomMemberListByRoomId(roomId, InviteState.INVITE);
//        log.info("memberList-Length: {}", memberList.size());
//
//        memberList.stream().forEach(roomMember -> {
//            Member member = roomMember.getMember();
//            log.info("member: {}", member.getId());
//        });
//    }
//
////    @Test
////    public void 패치조인_특정방에속한_유저리스트_조회() {
////        Long roomId = 1L;
////
////        List<RoomMember> memberList = roomMemberRepository.test(roomId, InviteState.INVITE);
////        log.info("memberList-Length: {}", memberList.size());
////
////        memberList.stream().forEach(roomMember -> {
////            Member member = roomMember.getMember();
////            log.info("member: {}", member.getId());
////        });
////    }
//
//}
