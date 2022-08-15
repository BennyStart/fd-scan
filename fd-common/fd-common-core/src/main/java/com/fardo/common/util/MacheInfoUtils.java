package com.fardo.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MacheInfoUtils {

	private static final String[] windowsCommand = { "ipconfig", "/all" };
	private static final String[] linuxCommand = { "/sbin/ifconfig", "-a" };
	private static final Pattern macPattern = Pattern.compile(".*((:?[0-9a-f]{2}[-:]){5}[0-9a-f]{2}).*", Pattern.CASE_INSENSITIVE);

	private MacheInfoUtils() {
	}

	public static List<String> getMacAddressList() {
		List<String> macAddressList = new ArrayList<String>();
		String command[];
		String os = getOsName();
		if (os.startsWith("Windows")) {
			command = windowsCommand;
		} else if (os.startsWith("Linux")) {
			command = linuxCommand;
		} else {
			throw new RuntimeException("Unknow operating system:" + os);
		}
		try {
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			for (String line = null; (line = bufReader.readLine()) != null;) {
				Matcher matcher = macPattern.matcher(line);
				if (matcher.matches()) {
					macAddressList.add(matcher.group(1));
					// macAddressList.add(matcher.group(1).replaceAll("[-:]",
					// ""));//去掉MAC中的“-”
				}
			}
			process.destroy();
			bufReader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return macAddressList;
	}

	public static String getOsName() {
		String os = "";
		os = System.getProperty("os.name");
		return os;
	}
	
	public static String getOsBit(){
		String version = System.getProperty("os.arch");
		if(version != null && version.contains("64")){
			return "64";
		}
		return "32";
	}

	/**
	 * 获取CPU号
	 * 
	 * @return
	 */
	public static String getCPUSerial() {
		String result = "";
		String os = getOsName();
		if (os.startsWith("Windows")) {
			try {
				File file = File.createTempFile("tmp", ".vbs");
				file.deleteOnExit();
				FileWriter fw = new FileWriter(file);

				String vbs = "On Error Resume Next \r\n\r\n" + "strComputer = \".\"  \r\n" + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
						+ "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
						+ "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n " + "For Each objItem in colItems\r\n "
						+ "    Wscript.Echo objItem.ProcessorId  \r\n " + "    exit for  ' do the first cpu only! \r\n" + "Next                    ";

				fw.write(vbs);
				fw.close();
				Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = input.readLine()) != null) {
					if (!result.isEmpty()) {
						result += "#";
					}
					result += line;
				}
				input.close();
				file.delete();
			} catch (Exception e) {
				e.fillInStackTrace();
			}
		} else if (os.startsWith("Linux")) {
			String CPU_ID_CMD = "dmidecode -t 4 | grep ID |sort -u |awk -F': ' '{print $2}'";
			Process p;
			try {
				p = Runtime.getRuntime().exec(new String[] { "sh", "-c", CPU_ID_CMD });// 管道
				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					if (!result.isEmpty()) {
						result += "#";
					}
					result += line;
				}
				br.close();
			} catch (IOException e) {
			}
		}
		/*
		 * if (StringUtils.isBlank(result)) { result = "NONE CPU"; }
		 */
		return result.trim();
	}

	public static String sort(String str) {
		// 利用toCharArray可将字符串转换为char型的数组
		char[] s1 = str.toCharArray();
		for (int i = 0; i < s1.length; i++) {
			for (int j = 0; j < i; j++) {
				if (s1[i] < s1[j]) {
					char temp = s1[i];
					s1[i] = s1[j];
					s1[j] = temp;
				}
			}
		}
		// 再次将字符数组转换为字符串，也可以直接利用String.valueOf(s1)转换
		String st = new String(s1);
		return st;
	}

	/**
	 * Returns the MAC address of the computer.
	 * 
	 * @return the MAC address
	 */
	public static String macKey() {
		try {
			String mac = Arrays.toString(getMacAddressList().toArray());
			String macSort = sort(mac);
			return MD5Util.MD5(macSort);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String cpuKey() {
		try {
			String cpuInfo = getCPUSerial();
			String sortCPU = sort(cpuInfo);
			return MD5Util.MD5(sortCPU);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		System.out.println("cpuKey:" + cpuKey());
		System.out.println("MAC:" + macKey());
		System.out.println(getOsBit());
	}
}
