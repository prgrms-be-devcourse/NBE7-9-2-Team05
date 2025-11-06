package com.back.motionit.support;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.back.motionit.global.service.AwsCdnSignService;
import com.back.motionit.global.service.AwsS3Service;

@IntegrationTest
public class BaseIntegrationTest {
	@MockitoBean
	AwsCdnSignService awsCdnSignService;

	@MockitoBean
	AwsS3Service awsS3Service;
}
