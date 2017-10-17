package com.newcare.param.type;

import java.util.ArrayList;
import java.util.List;

public class LongListParam extends AbstractParam {

	public LongListParam(String name, boolean required) {
		super(name, required);
	}

	public boolean isValid(Object obj) {
		if (required && obj == null)
			return false;
		if (!required && obj == null)
			return true;

		if (!(obj instanceof ArrayList))
			return false;

		List<Object> objList = (List<Object>) obj;

		for (Object o : objList) {
			if (o instanceof Integer || o instanceof Long) {
				continue;
			}
			return false;
		}

		return true;
	}

}
