/**
 * ponycar.com Inc.
 * Copyright (c) 2014-2015 All Rights Reserved.
 */

package com.ponycar.httpserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @description 实现url到handle的映射
 * @author chenzehe
 * @email chenzehe@wasu.com
 * @create 2015年3月31日 下午8:23:29
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RequestMapping {
	public String value();
}
