package com.jiangnan.arpattact.util;

/**
 * ����Ϊ�����࣬�ں�����ת����ͦ���á�
 * @author �����
 * @since 2014/4/20
 */
public class NetUtil {

	/**��byte�����ڵ�mac��ַת��Ϊ�ַ���
	 * @param s byte���飬���ж����Ʊ�ʾ��MAC��ַ
	 * @return MAC��ַ���ַ�����ʾ
	 */
	public static String macToString(byte[] s){
		String mac = "";
		for (int i = 0; i < s.length; i++){
			String temp = Integer.toHexString(s[i] & 0xff);
			if(temp.length() == 1) temp = "0" + temp;	//��0
			mac += temp;
		}
		return mac;
	}
	
	/**��byte�����ip��ַת��Ϊ�ַ���
	 * @param s �����Ʊ�ʾ��ip��ַ
	 * @return IP��ַ���ַ�����ʾ
	 */
	public static String ipToString(byte[] s){
		String ip = "";
		for (int i = 0; i < s.length; i++){
			String temp = Integer.toString(s[i] & 0xff);
			if(temp.length() == 1) temp = "0" + temp;	//��0
			ip += temp;
		}
		return ip;
	}
	
	/**�ж�IP���ݱ�Э������
	 * @param a IP����Э��������ֵ
	 * @return �ַ�����ʽ��Э������
	 */
	public static String getProtocol(int a){
		switch(a){
		case 0: return "HOPOPT";	//����ѡ�� 
		case 1: return "ICMP";		//Internet������Ϣ
		case 2:	return "IGMP";		//Internet�����
		case 4:	return "IP";		//
		case 6: return "TCP";
		case 17: return "UDP";
		case 41: return "IPv6";
		case 43: return "IPv6_Route";	//IPv6��·�ɱ�ͷ
		case 44: return "IPv6_Frag";	//IPv6��Ƭ�α�ͷ
		case 58: return "IPv6_ICMP";	//IPv6_ICMP
		case 59: return "IPv6_NoNxt";	//���� IPv6 ������һ����ͷ
		case 60: return "IPv6_Opts";	//IPv6 ��Ŀ��ѡ��
		default:	return "Other";
		}
	}
	
	/**��ȡARP���ݱ���Ӳ������
	 * @param a Ӳ������hardtype
	 * @return �ַ�����ʾ
	 */
	public static String getHardType(int a){
		switch(a){
		case 1: return "��̫��";
		case 6: return "IEEE802";
		case 15: return "Frame-Relay(֡�м�)";
		default: return "Other";
		}
	}
	
	/**ARP���ݱ���������
	 * @param a ����������ֵ
	 * @return �ַ�����ʾ
	 */
	public static String getOperationType(int a){
		switch(a){
		case 1: return "ARP����";
		case 2: return "ARPӦ��";
		case 3: return "RARP����";
		case 4: return "RARPӦ��";
		default: return "Other";
		}
	}
}
