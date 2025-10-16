package com.back.motionit.domain.challenge.room.builder;

import static org.springframework.util.StringUtils.*;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import net.datafaker.Faker;

public class CreateRoomRequestBuilder {

	private static final Faker faker = new Faker(new Locale("ko"));
	private String title;
	private String description;
	private int capacity;
	private int duration;
	private String videoUrl;

	public CreateRoomRequestBuilder() {
		this.title = truncate(faker.lorem().characters(8, 30, true), 30);
		this.description = truncate(faker.lorem().characters(20, 100, true), 100);
		this.capacity = faker.number().numberBetween(2, 100);
		this.duration = faker.number().numberBetween(3, 30);
		this.videoUrl = "https://youtube.com/watch?v=" + faker.regexify("[A-Za-z0-9_-]{11}");
	}

	public Map<String, String> toParamMap() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("title", title);
		map.put("description", description);
		map.put("capacity", String.valueOf(capacity));
		map.put("duration", String.valueOf(duration));
		map.put("videoUrl", videoUrl);
		return map;
	}

	public CreateRoomRequestBuilder title(String title) {
		this.title = title;
		return this;
	}

	public CreateRoomRequestBuilder description(String description) {
		this.description = description;
		return this;
	}

	public CreateRoomRequestBuilder capacity(int capacity) {
		this.capacity = capacity;
		return this;
	}

	public CreateRoomRequestBuilder duration(int duration) {
		this.duration = duration;
		return this;
	}

	public CreateRoomRequestBuilder videoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getDuration() {
		return duration;
	}

	public String getVideoUrl() {
		return videoUrl;
	}
}
