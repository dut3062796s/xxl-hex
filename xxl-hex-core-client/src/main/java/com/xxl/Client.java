package com.xxl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;

import com.xxl.demo.msg.request.DemoRequest;
import com.xxl.demo.msg.response.DemoResponse;
import com.xxl.hex.serialise.ByteHexConverter;

public class Client {

	public static void main(String[] args) {
		synchronous();
		// asynchronous();
	}

	public static void synchronous() {

		HttpClient client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);// 还可以进行其他的配置
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			client.start();
			ContentExchange contentExchange = new ContentExchange();
			client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
			contentExchange.setURL("http://127.0.0.1:8080");
			contentExchange.setTimeout(30000);
			
			client.send(contentExchange);// client发送
			
			contentExchange.waitForDone();// 同步等待结果返回
			System.err.println("Responsestatus:" + contentExchange.getResponseStatus());
			System.out.println("Responsecontent:" + contentExchange.getResponseContent());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 异步
	 */
	public static void asynchronous() {
		HttpClient client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);// 还可以进行其他的配置
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ContentExchange exchange = new ContentExchange() {
			@Override
			protected void onResponseComplete() throws IOException {
				super.onResponseComplete();
				String responseContent = this.getResponseContent();
				System.out.println(responseContent);
				// 可以对返回结果自由发挥了
			}
		};
		exchange.setMethod("GET");
		exchange.setURL("http://127.0.0.1:8080");

		try {
			client.send(exchange);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main2(String[] args) throws InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		DemoRequest request = new DemoRequest();
		request.setParam("哈喽，我是client。");

		String request_hex = ByteHexConverter.byte2hex(request.toHexByte());
		System.out.println(request_hex); // 636F6D2E78786C2E64656D6F2E6D73672E726571756573742E44656D6F5265717565737400000000000000000000000000000000000000000000000000000000E59388E596BDEFBC8CE68891E698AF636C69656E

		// 通讯中...

		String response_hex = "C8000000737563636573730000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000007265733AE59388E596BDEFBC8CE68891E698AF63";
		DemoResponse msg = (DemoResponse) DemoResponse.class.newInstance()
				.fillHexByte(ByteHexConverter.hex2Byte(response_hex));
		System.out.println(BeanUtils.describe(msg));

	}

}