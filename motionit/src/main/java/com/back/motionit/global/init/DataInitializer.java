package com.back.motionit.global.init;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
@RequiredArgsConstructor
public class DataInitializer {

	private final UserRepository userRepository;
	private final ChallengeRoomRepository challengeRoomRepository;
	private final ChallengeParticipantRepository challengeParticipantRepository;
	private final ChallengeVideoRepository challengeVideoRepository;
	private final ChallengeMissionStatusRepository challengeMissionStatusRepository;

	@Bean
	public ApplicationRunner initDummyData() {
		return args -> {

			if (challengeRoomRepository.count() > 0) {
				log.info("✅ 이미 데이터가 존재합니다. 더미 데이터 생성을 스킵합니다.");
				return;
			}

			log.info("🚀 더미 데이터 생성 시작");

			// 1️⃣ 유저 생성
			User host = userRepository.save(User.builder()
				.kakaoId(1001L)
				.email("host@example.com")
				.nickname("방장유저")
				.password("1234")
				.loginType(LoginType.KAKAO)
				.userProfile("https://picsum.photos/100?1")
				.build());

			List<User> users = IntStream.range(0, 5)
				.mapToObj(i -> userRepository.save(User.builder()
					.kakaoId(2000L + i)
					.email("user" + i + "@example.com")
					.nickname("참가자" + (i + 1))
					.password("pass" + i)
					.loginType(LoginType.KAKAO)
					.userProfile("https://picsum.photos/100?" + (i + 2))
					.build()))
				.collect(Collectors.toList());

			// 2️⃣ 운동방 생성
			ChallengeRoom room = new ChallengeRoom(
				host,
				"🔥 2주 챌린지 프로젝트",
				"매일 운동 영상을 올리고 인증하는 방입니다.",
				10,
				OpenStatus.OPEN,
				LocalDateTime.now().plusDays(1),  // 내일부터 시작
				LocalDateTime.now().plusDays(15),
				"https://picsum.photos/600/300?challenge",
				new ArrayList<>()
			);
			challengeRoomRepository.save(room);

			// 3️⃣ 참가자 생성 (방장 + 일반 참가자들)
			ChallengeParticipant hostParticipant = ChallengeParticipant.builder()
				.user(host)
				.challengeRoom(room)
				.role(ChallengeParticipantRole.HOST)
				.quited(false)
				.challengeStatus(false)
				.build();

			List<ChallengeParticipant> participants = users.stream()
				.map(u -> ChallengeParticipant.builder()
					.user(u)
					.challengeRoom(room)
					.role(ChallengeParticipantRole.NORMAL)
					.quited(false)
					.challengeStatus(false)
					.build())
				.collect(Collectors.toList());

			challengeParticipantRepository.save(hostParticipant);
			challengeParticipantRepository.saveAll(participants);

			// 4️⃣ 영상 생성 (오늘 + 어제)
			ChallengeVideo todayVideo = ChallengeVideo.builder()
				.challengeRoom(room)
				.user(host)
				.youtubeVideoId("yt_today_ABC123")
				.title("오늘의 미션 영상 🏋️")
				.thumbnailUrl("https://img.youtube.com/vi/yt_today_ABC123/0.jpg")
				.duration(320)
				.uploadDate(LocalDate.now())
				.isTodayMission(true)
				.build();

			ChallengeVideo oldVideo = ChallengeVideo.builder()
				.challengeRoom(room)
				.user(host)
				.youtubeVideoId("yt_yesterday_DEF456")
				.title("어제의 복근운동 영상 💪")
				.thumbnailUrl("https://img.youtube.com/vi/yt_yesterday_DEF456/0.jpg")
				.duration(280)
				.uploadDate(LocalDate.now().minusDays(1))
				.isTodayMission(false)
				.build();

			challengeVideoRepository.save(todayVideo);
			challengeVideoRepository.save(oldVideo);

			// 5️⃣ 미션 상태 생성
			List<ChallengeMissionStatus> missionStatuses = new ArrayList<>();

			missionStatuses.add(new ChallengeMissionStatus(
				hostParticipant,
				todayVideo,
				LocalDate.now(),
				true
			));

			for (ChallengeParticipant p : participants) {
				boolean completed = new Random().nextBoolean();
				missionStatuses.add(new ChallengeMissionStatus(
					p,
					completed ? todayVideo : null,
					LocalDate.now(),
					completed
				));
			}
		};
	}
}
