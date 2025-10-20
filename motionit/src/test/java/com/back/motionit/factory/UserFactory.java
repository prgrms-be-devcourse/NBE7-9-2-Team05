package com.back.motionit.factory;

import com.back.motionit.domain.user.entity.LoginType;
import com.back.motionit.domain.user.entity.User;

public final class UserFactory extends BaseFactory {
	public static User fakeUser() {
		return User.builder()
			.kakaoId(faker.number().randomNumber())
			.email(faker.internet().emailAddress())
			.nickname(faker.name().firstName())
			.password(faker.name().firstName())
			.password(faker.internet().password(8, 16))
			.loginType(faker.options().option(LoginType.class))
			.userProfile(faker.internet().url())
			.build();
	}
}
