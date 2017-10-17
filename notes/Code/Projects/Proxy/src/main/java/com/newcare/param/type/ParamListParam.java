package com.newcare.param.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.newcare.mesg.MessageService;

public class ParamListParam extends AbstractParam {

	@Autowired
	private MessageService mesgService;

	private Param[] names;

	public ParamListParam(String name, boolean required) {
		super(name, required);
	}

	public ParamListParam(String name, boolean required, Param[] names) {
		super(name, required);
		this.names = names;
	}

	public boolean isValid(Object obj) {
		if (required && obj == null)
			return false;
		if (!required && obj == null)
			return true;

		if (!(obj instanceof ArrayList))
			return false;

		List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			for (Param param : names) {
				Object val = map.get(param.getName());
				if (!param.isValid(val)) {
					return false;
				}
			}

		}
		return true;
	}

	public Param[] getNames() {
		return names;
	}

	public void setNames(Param[] names) {
		this.names = names;
	}

}
