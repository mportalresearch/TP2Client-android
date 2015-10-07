package com.mportal.handlemanagement.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import com.mportal.logger.SIPLogger;

public class HttpManager {

	public static void sendRequest(String uri, String confId) {

		URL url = null;
		OutputStream out = null;
		HttpsURLConnection httpURLConnection = null;

		try {

			url = new URL(uri);

			httpURLConnection = (HttpsURLConnection) url.openConnection();

			// httpURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "keep-alive");
			httpURLConnection.setRequestProperty("Content-Type", "text/xml");
			httpURLConnection.setRequestProperty("SendChunked", "True");
			httpURLConnection.setRequestProperty("UseCookieContainer", "True");
			HttpURLConnection.setFollowRedirects(false);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setUseCaches(true);
			byte[] soapBody = getReqData(uri, confId);

			httpURLConnection.setRequestProperty("Content-length",
					soapBody.length + "");
			httpURLConnection.setReadTimeout(10 * 1000);
			// httpURLConnection.setConnectTimeout(10 * 1000);
			httpURLConnection.connect();

			out = httpURLConnection.getOutputStream();

			if (out != null) {
				out.write(soapBody);
				out.flush();
			}

			processResponse(httpURLConnection);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
				httpURLConnection = null;
			}

			if (out != null) {
				out = null;
			}
		}
	}

	private static void processResponse(HttpsURLConnection httpURLConnection) {

		OutputStream out = null;
		int respCode = -1;

		try {
			if (httpURLConnection != null) {
				respCode = httpURLConnection.getResponseCode();
				SIPLogger.e("respCode", ":" + respCode);

				if (respCode == 200) {
					InputStream responce = httpURLConnection.getInputStream();
					String str = convertStreamToString(responce);
					System.out.println(".....data....." + new String(str));
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out = null;
			}
		}

	}

	//
	// private boolean doWebClientJoinRequest(String ip, String sipCallId) {
	//
	// OutputStream out = null;
	// int respCode = -1;
	// boolean isSuccess = false;
	// URL url = null;
	// HttpsURLConnection httpURLConnection = null;
	//
	// try {
	//
	// url = new URL(ip);
	//
	// httpURLConnection = (HttpsURLConnection) url.openConnection();
	//
	// do {
	// // httpURLConnection.setHostnameVerifier(DO_NOT_VERIFY);
	// httpURLConnection.setRequestMethod("POST");
	// httpURLConnection
	// .setRequestProperty("Connection", "keep-alive");
	// httpURLConnection
	// .setRequestProperty("Content-Type", "text/xml");
	// httpURLConnection.setRequestProperty("SendChunked", "True");
	// httpURLConnection.setRequestProperty("UseCookieContainer",
	// "True");
	// HttpURLConnection.setFollowRedirects(false);
	// httpURLConnection.setDoOutput(true);
	// httpURLConnection.setDoInput(true);
	// httpURLConnection.setUseCaches(true);
	// httpURLConnection.setRequestProperty("Content-length",
	// getReqData().length + "");
	// httpURLConnection.setReadTimeout(10 * 1000);
	// // httpURLConnection.setConnectTimeout(10 * 1000);
	// httpURLConnection.connect();
	//
	// out = httpURLConnection.getOutputStream();
	//
	// if (out != null) {
	// out.write(getReqData());
	// out.flush();
	// }
	//
	// if (httpURLConnection != null) {
	// respCode = httpURLConnection.getResponseCode();
	// SIPLogger.e("respCode", ":" + respCode);
	//
	// }
	// } while (respCode == -1);
	//
	// // If it works fine
	// if (respCode == 200) {
	//
	// isSuccess = true;
	// try {
	// InputStream responce = httpURLConnection.getInputStream();
	// String str = convertStreamToString(responce);
	// System.out.println(".....data....." + new String(str));
	//
	// // String str
	// //
	// =Environment.getExternalStorageDirectory().getAbsolutePath()+"/sunilwebservice.txt";
	// // File f = new File(str);
	// //
	// // try{
	// // f.createNewFile();
	// // FileOutputStream fo = new FileOutputStream(f);
	// // fo.write(b);
	// // fo.close();
	// // }catch(Exception ex){
	// // ex.printStackTrace();
	// // }
	// } catch (Exception e1) {
	// e1.printStackTrace();
	// }
	// } else {
	// isSuccess = false;
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// if (out != null) {
	// out = null;
	// }
	// if (httpURLConnection != null) {
	// httpURLConnection.disconnect();
	// httpURLConnection = null;
	// }
	// }
	// return isSuccess;
	// }

	public static String createSoapHeader(String uri) {
		String soapHeader = null;

		// soapHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
		// + "<soap:Envelope "
		// + "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\""
		// + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
		// + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"" + ">";

		if (uri.equalsIgnoreCase(WebCollabUrls.WEB_CLIENT_JOIN_REQUEST_URL)) {
			soapHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<SOAP-ENV:Envelope SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\""
					+ "xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/ \""
					+ "xmlns:SOAP-ENV=\"htt://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<SOAP-ENV:Header>" + "<targetIP>47.168.89.77</targetIP>"
					+ "<targetPort>8086</targetPort>"
					+ "<TrxnID>12346</TrxnID>"
					+ "<clientIP>10.40.1.107-12962298570852</clientIP>"
					+ "</SOAP-ENV:Header>";
		}

		return soapHeader;
	}

	public static byte[] getReqData(String uri, String conferenceId) {
		StringBuilder requestData = new StringBuilder();

		requestData.append(createSoapHeader(uri));

		String body = null;

		if (uri.equalsIgnoreCase(WebCollabUrls.WEB_CLIENT_JOIN_REQUEST_URL)) {
			body = "<SOAP-ENV:Body>"
					+ "<m:WebClientJoinRequest xmlns:m=\"http://schemas.pactolus.com\">"
					+ "<conferenceID>" + conferenceId + "</conferenceID>"
					+ "<moderatorEvents>F</moderatorEvents>"
					+ "<csrEvents>F</csrEvents>" + "</m:WebClientJoinRequest>"
					+ "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>";
		}

		if (body != null) {
			requestData.append(body);
		}

		return requestData.toString().trim().getBytes();
	}

	private static String convertStreamToString(InputStream is)
			throws UnsupportedEncodingException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}
}
