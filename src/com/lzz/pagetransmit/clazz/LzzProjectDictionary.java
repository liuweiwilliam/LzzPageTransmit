package com.lzz.pagetransmit.clazz;

import java.util.ArrayList;
import java.util.List;

public class LzzProjectDictionary {
	private String dictionaryName; //目录名称
	private String path; //详细路径
	private List<LzzProjectDictionary> subDictionary = new ArrayList<LzzProjectDictionary>(); //子目录
	
	public LzzProjectDictionary(){
		
	}

	public String getDictionaryName() {
		return dictionaryName;
	}

	public void setDictionaryName(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public List<LzzProjectDictionary> getSubDictionary() {
		return subDictionary;
	}

	public void setSubDictionary(List<LzzProjectDictionary> subDictionary) {
		this.subDictionary = subDictionary;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public LzzProjectDictionary(String name, String path){
		dictionaryName = name;
		this.path = path;
	}
	
	public LzzProjectDictionary(String name){
		dictionaryName = name;
		this.path = name;
	}
	
	public String getPathByName(String name){
		if(name.equals(dictionaryName)){
			return path;
		}
		
		if(null==subDictionary) return null;
		for(int i=0; i<subDictionary.size(); i++){
			String rslt = subDictionary.get(i).getPathByName(name);
			if(null!=rslt){
				return rslt;
			}
		}
		
		return null;
	}
	
	public LzzProjectDictionary getLzzProjectDictionaryByName(String name){
		if(name.equals(dictionaryName)){
			return this;
		}
		
		if(null==subDictionary) return null;
		for(int i=0; i<subDictionary.size(); i++){
			LzzProjectDictionary rslt = subDictionary.get(i).getLzzProjectDictionaryByName(name);
			if(null!=rslt){
				return rslt;
			}
		}
		
		return null;
	}
	
	public LzzProjectDictionary getLzzProjectDictionaryByPath(String path){
		if(path.equals(path)){
			return this;
		}
		
		if(null==subDictionary) return null;
		for(int i=0; i<subDictionary.size(); i++){
			LzzProjectDictionary rslt = subDictionary.get(i).getLzzProjectDictionaryByPath(path);
			if(null!=rslt){
				return rslt;
			}
		}
		
		return null;
	}
	
	public void addSubDictionary(String name){
		LzzProjectDictionary sub = new LzzProjectDictionary(name);
		sub.setPath(this.getPath() + name);
		subDictionary.add(sub);
	}
	
	public String toString(){
		String rslt = "";
		
		rslt = path + "\n";
		
		if(null==subDictionary) return rslt;
		
		for(int i=0; i<subDictionary.size(); i++){
			rslt += subDictionary.get(i).toString() + "\n";
		}
		
		return rslt;
	}
}
