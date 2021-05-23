package me.hope.exception;

import me.hope.core.inject.annotation.NotSingleton;

/**
 * 当激活码类别不存在时返回
 */
@NotSingleton
public class GiftNotFoundException extends Exception{
}
