package com.csair.csmbp.brave;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Util {
	public static int ip2Int(String ip) {
		if (ip == null)
			return 0;
		String[] ips = ip.split("\\.");
		int ipInt = 0;
		int value;
		int w = 24;
		for (String s : ips) {
			try {
				value = Integer.valueOf(s);
			} catch (Exception e) {
				value = 0;
			}
			ipInt |= value << w;
			w -= 8;
		}
		return ipInt;
	}

	public static void main(String... strings) {
		CompletableFuture<Boolean> f = CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		});
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		f.thenApply((v) -> {
			System.out.println("aa");
			return null;
		});
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
