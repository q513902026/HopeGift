package me.hope.exception;

import me.hope.core.inject.annotation.NotSingleton;

/**
 * 当激活码不存在时返回
 */
@NotSingleton
public class CDKNotFoundException extends Exception{
}
