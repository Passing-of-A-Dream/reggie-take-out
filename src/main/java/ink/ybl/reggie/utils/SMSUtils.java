package ink.ybl.reggie.utils;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import darabonba.core.client.ClientOverrideConfiguration;

import java.util.concurrent.CompletableFuture;

public class SMSUtils {
    public static void main(String[] args) throws Exception {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId("")
                .accessKeySecret("")
                .build());

        AsyncClient client = AsyncClient.builder()
                .region("cn-chengdu")
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                )
                .build();

        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .phoneNumbers("18600000000")
                .signName("阿里云短信测试专用")
                .templateCode("SMS_1000000")
                .templateParam("{\"code\":\"12345\"}")
                .build();

        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);

        SendSmsResponse sendSmsResponse = response.get();

        System.out.println(sendSmsResponse);

        client.close();
    }
}
