package com.common.as.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Ƶ��ͳ��
 * @author wzp
 *
 */
public class Frequency {
	//�����ĳ��ֵĴ����Map
	private Map<String, Integer> map = new HashMap<String, Integer>();
	//��Ƶ�ʽ�������
	private Set<Entiry> set = new TreeSet<Entiry>();
	//ֵֻ����һ�����ת��
	private boolean changeFlag = true;

	/**
	 * ����ͳ��
	 * 
	 * @param t
	 */
	public void addStatistics(String t) {
		Integer reg = map.get(t);
		map.put(t, reg == null ? 1 : reg + 1);
	}

	/**
	 * ����ݽ�������
	 */
	private void dataChanged() {
		if (changeFlag) {
			Iterator<String> it = map.keySet().iterator();
			String key = null;

			while (it.hasNext()) {
				key = it.next();
				set.add(new Entiry(key, map.get(key)));
				changeFlag = false;
			}
		}
	}

	/**
	 * �õ����ֵ����
	 * 
	 * @return
	 */
	public Entiry getMaxValueItem() {
		dataChanged();
		Iterator<Entiry> it = set.iterator();
		Entiry fre = null;
		if (it.hasNext()) {
			fre = it.next();
			//System.out.println(fre.toString());
		}
		return fre;
	}

	public List<Entiry> getDataDesc() {
		dataChanged();
		Iterator<Entiry> it = set.iterator();
		List< Entiry> list = new ArrayList<Entiry>();
		while (it.hasNext()) {
			Entiry fre=it.next();
			list.add(fre);
		//	System.out.println(fre.toString());
		}
		return list;
	}

	public static void main(String[] args) {
		Frequency f = new Frequency();
		f.addStatistics("w");
		f.addStatistics("w");
		f.addStatistics("w");

		f.addStatistics("a");

		f.addStatistics("z");
		f.addStatistics("z");
		f.addStatistics("z");
		f.addStatistics("z");

		f.addStatistics("c");
		f.addStatistics("c");
		f.addStatistics("c");
		f.addStatistics("c");

		f.getMaxValueItem();
		f.getDataDesc();
	}

	public class Entiry implements Comparable<Entiry> {
	    private String key;
	    private Integer count;
	    public Entiry (String key, Integer count) {
	        this.key = key;
	        this.count = count;
	    }
	    public int compareTo(Entiry o) {
	        int cmp = count.intValue() - o.count.intValue();
	        return (cmp == 0 ? ( key).compareTo( o.key) : -cmp);
	        //ֻ��������һ��žͿ��Ծ��������ǽ�������  -cmp�������У�cmp��������
	        //��ΪTreeSet�����WorkForMap��compareTo����4���Լ�������
	    }

	    @Override
	    public String toString() {
	        return key + " s" + count;
	    }

	    public String getKey() {
	        return key;
	    }

	    public Integer getCount() {
	        return count;
	    }
	}
}
