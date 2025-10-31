package com.javaweb.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

public class MapUtils {
	public static <T> T getObject(Map<String, Object> maps, String key, Class<T> tClass) {
		Object obj = maps.getOrDefault(key,null);
		if(obj != null) {
			if(tClass.getTypeName().equals("java.lang.String")) {
				obj = obj != null ? obj.toString() : null;
			} else if (tClass.getTypeName().equals("java.lang.Long")) {
				obj = obj != null ? Long.valueOf(obj.toString()) : null;
			} else if (tClass.getTypeName().equals("java.lang.Double")) {
				obj = obj != null ? Double.valueOf(obj.toString()) : null;
			} else {
				obj = obj != null ? Integer.valueOf(obj.toString()) : null;
			}
			return tClass.cast(obj);
		}
		return null;
	}
	
	public static <T> boolean getObjectTwo(Object object, Class<T> tClass) {
		if(object != null) {
			if(tClass.getTypeName().equals("java.lang.String")) {
				object = object != null ? object.toString() : null;
			} else if(tClass.getTypeName().equals("java.lang.Long")) {
				object = object != null ? Long.valueOf(object.toString()) : null;
			} else if(tClass.getTypeName().equals("java.lang.Double")) {
				object = object != null ? Double.valueOf(object.toString()) : null;
			} else {
				object = object != null ? Integer.valueOf(object.toString()) : null;
			}
			return true;
		}
		return false;
	}
	
	public static LocalDateTime toLocalDateTime(Object value) {
	    if (value == null) return null;
	    if (value instanceof java.sql.Timestamp) {
	        return ((java.sql.Timestamp) value).toLocalDateTime();
	    }
	    if (value instanceof java.util.Date) {
	        return ((java.util.Date) value).toInstant()
	                .atZone(ZoneId.systemDefault())
	                .toLocalDateTime();
	    }
	    throw new IllegalArgumentException("Unsupported type: " + value.getClass());
	}

}
