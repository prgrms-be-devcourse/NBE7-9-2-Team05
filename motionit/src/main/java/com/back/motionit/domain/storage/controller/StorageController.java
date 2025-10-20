package com.back.motionit.domain.storage.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.storage.api.StorageApi;
import com.back.motionit.domain.storage.api.response.StorageHttp;
import com.back.motionit.domain.storage.dto.CdnUrlResponse;
import com.back.motionit.domain.storage.dto.CreateUploadUrlRequest;
import com.back.motionit.domain.storage.dto.UploadUrlResponse;
import com.back.motionit.global.respoonsedata.ResponseData;
import com.back.motionit.global.service.AwsCdnSignService;
import com.back.motionit.global.service.AwsS3Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storage")
public class StorageController implements StorageApi {

	private final AwsS3Service s3Service;
	private final AwsCdnSignService cdnSignService;

	@Override
	public ResponseData<UploadUrlResponse> createUploadUrl(@RequestBody @Valid CreateUploadUrlRequest request) {
		String objectKey = (request.objectKey() == null || request.objectKey().isBlank())
			? s3Service.buildObjectKey(request.originalFileName())
			: request.objectKey();

		String url = s3Service.createUploadUrl(
			objectKey,
			request.contentType()
		);

		return ResponseData.success(
			StorageHttp.CREATE_AWS_URL_SUCCESS_CODE,
			StorageHttp.CREATE_AWS_URL_SUCCESS_MESSAGE,
			new UploadUrlResponse(objectKey, url)
		);
	}

	@Override
	public ResponseData<CdnUrlResponse> signCdnUrl(@RequestParam("key") String key) {
		String url = cdnSignService.sign(key);
		return ResponseData.success(
			StorageHttp.CREATE_CDN_URL_SUCCESS_CODE,
			StorageHttp.CREATE_CDN_URL_SUCCESS_MESSAGE,
			new CdnUrlResponse(key, url)
		);
	}
}
