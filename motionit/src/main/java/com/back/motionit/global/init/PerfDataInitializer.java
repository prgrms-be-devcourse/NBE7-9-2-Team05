package com.back.motionit.global.init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;
import com.back.motionit.domain.challenge.mission.repository.ChallengeMissionStatusRepository;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipantRole;
import com.back.motionit.domain.challenge.participant.repository.ChallengeParticipantRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;
import com.back.motionit.domain.challenge.video.repository.ChallengeVideoRepository;
import com.back.motionit.domain.user.entity.LoginType;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Profile("perf")  // ✅ perf 프로파일에서만 작동
@RequiredArgsConstructor
public class PerfDataInitializer {
	private final UserRepository userRepository;
	private final ChallengeRoomRepository challengeRoomRepository;
	private final ChallengeParticipantRepository challengeParticipantRepository;
	private final ChallengeVideoRepository challengeVideoRepository;
	private final ChallengeMissionStatusRepository challengeMissionStatusRepository;

	@Bean
	public ApplicationRunner initPerfDummyData() {
		return args -> {
			if (challengeRoomRepository.count() > 0) {
				log.info("✅ perf 데이터 이미 존재. 초기화 스킵");
				return;
			}

			log.info("🚀 perf 프로파일용 더미 데이터 생성 시작");

			// 1️⃣ 유저 생성
			User host = userRepository.save(User.builder()
				.kakaoId(9001L)
				.email("perf_host@example.com")
				.nickname("PerfHost")
				.password("1234")
				.loginType(LoginType.KAKAO)
				.userProfile("https://picsum.photos/100?perf1")
				.build());

			List<User> users = IntStream.range(1, 11)
				.mapToObj(i -> userRepository.save(User.builder()
					.kakaoId(9100L + i)
					.email("perf_user" + i + "@example.com")
					.nickname("PerfUser" + i)
					.password("pass" + i)
					.loginType(LoginType.KAKAO)
					.userProfile("https://picsum.photos/100?perf" + (i + 1))
					.build()))
				.collect(Collectors.toList());

			// 2️⃣ 챌린지 방 생성
			ChallengeRoom room = challengeRoomRepository.save(new ChallengeRoom(
				host,
				"🔥 K6 부하테스트 전용 방",
				"부하테스트용 방입니다.",
				50,
				OpenStatus.OPEN,
				LocalDateTime.now().minusDays(1),
				LocalDateTime.now().plusDays(7),
				"images/test/perf_room.png",
				null,
				new ArrayList<>(),
				new ArrayList<>()
			));

			// 3️⃣ 참가자 생성
			ChallengeParticipant hostParticipant = challengeParticipantRepository.save(
				ChallengeParticipant.builder()
					.user(host)
					.challengeRoom(room)
					.role(ChallengeParticipantRole.HOST)
					.quited(false)
					.challengeStatus(false)
					.build()
			);

			List<ChallengeParticipant> participants = users.stream()
				.map(u -> ChallengeParticipant.builder()
					.user(u)
					.challengeRoom(room)
					.role(ChallengeParticipantRole.NORMAL)
					.quited(false)
					.challengeStatus(false)
					.build())
				.collect(Collectors.toList());
			challengeParticipantRepository.saveAll(participants);

			// 4️⃣ 오늘의 영상 생성
			ChallengeVideo todayVideo = challengeVideoRepository.save(ChallengeVideo.builder()
				.challengeRoom(room)
				.user(host)
				.youtubeVideoId("yt_perf_today")
				.title("오늘의 퍼포먼스 테스트 영상")
				.thumbnailUrl("https://img.youtube.com/vi/yt_perf_today/0.jpg")
				.duration(300)
				.uploadDate(LocalDate.now())
				.isTodayMission(true)
				.build());

			// 5️⃣ 미션 상태
			List<ChallengeMissionStatus> missions = new ArrayList<>();
			missions.add(new ChallengeMissionStatus(hostParticipant, LocalDate.now()));

			for (ChallengeParticipant p : participants) {
				missions.add(new ChallengeMissionStatus(p, LocalDate.now()));
			}
			challengeMissionStatusRepository.saveAll(missions);

			log.info("🎯 perf 더미데이터 생성 완료! [roomId={}, users={}, videoId={}]",
				room.getId(), users.size(), todayVideo.getId());
		};
	}
}
